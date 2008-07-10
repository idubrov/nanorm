/**
 * Copyright (C) 2008 Ivan S. Dubrov
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.code.nanorm.internal;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.code.nanorm.NanormFactory;
import com.google.code.nanorm.ResultCallback;
import com.google.code.nanorm.Session;
import com.google.code.nanorm.TypeHandlerFactory;
import com.google.code.nanorm.config.SessionConfig;
import com.google.code.nanorm.exceptions.DataException;
import com.google.code.nanorm.internal.config.InternalConfiguration;
import com.google.code.nanorm.internal.config.StatementConfig;
import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.Setter;
import com.google.code.nanorm.internal.mapping.result.ResultCallbackSource;
import com.google.code.nanorm.internal.mapping.result.ResultCollectorUtil;
import com.google.code.nanorm.internal.mapping.result.RowMapper;
import com.google.code.nanorm.internal.session.SessionSpi;
import com.google.code.nanorm.internal.session.SingleConnSessionSpi;
import com.google.code.nanorm.internal.type.TypeHandler;

/**
 * Factory implementation.
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
public class FactoryImpl implements NanormFactory, QueryDelegate {

	/**
	 * Thread local that holds per-thread sessions.
	 */
	private final ThreadLocal<SessionSpi> sessions = new ThreadLocal<SessionSpi>();

	private final InternalConfiguration config;

	private final SessionConfig sessionSpiConfig;

	/**
	 * Constructor.
	 * 
	 * @param internalConfig factory configuration
	 * @param sessionConfig session configuration
	 */
	public FactoryImpl(InternalConfiguration internalConfig,
			SessionConfig sessionConfig) {
		this.config = internalConfig;
		this.sessionSpiConfig = sessionConfig;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public InternalConfiguration getInternalConfiguration() {
		return config;
	}

	/**
	 * @see com.google.code.nanorm.NanormFactory#createMapper(java.lang.Class)
	 */
	public <T> T createMapper(Class<T> mapperClass) {
		config.configure(mapperClass);

		// TODO: Check we mapped this class!
		return config.getIntrospectionFactory().createMapper(mapperClass,
				config, this);
	}

	/**
	 * {@inheritDoc}
	 */
	public Session openSession() {
		if (sessionSpiConfig == null) {
			throw new IllegalArgumentException("Session SPI is not configured!");
		}
		if (sessions.get() != null) {
			throw new IllegalStateException(
					"Session was already started for this thread!");
		}

		final SessionSpi spi = sessionSpiConfig.newSessionSpi();
		sessions.set(spi);
		return new TransactionImpl(spi);
	}

	/**
	 * {@inheritDoc}
	 */
	public Session openSession(Connection connection) {
		if (connection == null) {
			throw new IllegalArgumentException("Connection must not be null!");
		}
		if (sessions.get() != null) {
			throw new IllegalStateException(
					"Session was already started for this thread!");
		}

		final SessionSpi spi = new SingleConnSessionSpi(connection);
		sessions.set(spi);
		return new TransactionImpl(spi);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object query(StatementConfig stConfig, Object[] args) {
		// TODO: Refactor!

		// Request-scoped data
		Request request = new Request(this);

		SessionSpi spi = sessions.get();
		boolean isAuto = false;
		if (spi == null) {
			// Auto-create session for single request
			// TODO: Check if autosession is enabled
			isAuto = true;
			spi = sessionSpiConfig.newSessionSpi();
		}
		// Close session spi after this block if in auto mode
		try {
			Connection conn = spi.getConnection();
			// TODO: Log connection being used

			// TODO: Could cause NPE if return type is primitive
			Object result = null;

			// Generate key prior to mapping the parameters, so we
			// have a chance to update arguments with generated key
			result = selectKey(result, stConfig, false, args);

			// Bind fragment to arguments
			BoundFragment fragment = stConfig.getStatementBuilder()
					.bindParameters(args);

			// SQL, parameters and their types
			StringBuilder sql = new StringBuilder();
			List<Object> parameters = new ArrayList<Object>();
			List<Type> types = new ArrayList<Type>();

			// Fill SQL string builder, parameter and types
			fragment.generate(sql, parameters, types);

			// Close connection after this try
			try {
				// TODO: Log SQL
				PreparedStatement st = conn.prepareStatement(sql.toString());
				try {
					// Map parameters to the statement
					mapParameters(st, types, parameters);

					if (stConfig.isInsert()) {
						st.executeUpdate();

						result = selectKey(result, stConfig, true, args);
					} else if (stConfig.isUpdate()) {
						result = st.executeUpdate();
					} else {
						ResultSet rs = st.executeQuery();

						// Create callback that will receive the mapped objects
						ResultCallback<Object> callback = createResultCallback(
								stConfig, args, request);

						// Iterate through the result set
						RowMapper resultMapper = stConfig.getResultMapper();
						while (rs.next()) {
							resultMapper
									.processResultSet(request, rs, callback);
						}
						callback.finish();
						result = request.getResult();
					}

					if (result == null) {
						checkNotPrimitive(stConfig.getResultType());
					}
					return result;
				} finally {
					st.close();
				}
			} catch (SQLException e) {
				throw new DataException(
						"SQL exception occured while executing the query!", e);
			} finally {
				spi.releaseConnection(conn);
			}
		} finally {
			if (isAuto) {
				spi.end();
			}
		}
	}

	private ResultCallback<Object> createResultCallback(
			StatementConfig stConfig, Object[] args, Request request) {

		// If we have ResultCallback in parameters -- use it,
		// otherwise create callback which will set result to the
		// request.
		ResultCallback<Object> callback;
		if (stConfig.getCallbackIndex() != StatementConfig.RETURN_VALUE) {
			// This is OK, since we deduced result type exactly
			// from this parameter
			@SuppressWarnings("unchecked")
			ResultCallback<Object> temp = (ResultCallback<Object>) args[stConfig
					.getCallbackIndex()];
			callback = temp;
		} else {
			// Prepare result callback and process results
			ResultGetterSetter rgs = new ResultGetterSetter(stConfig
					.getResultType());
			ResultCallbackSource callbackSource = ResultCollectorUtil
					.createResultCallback(rgs, rgs, stConfig);

			callback = callbackSource.forInstance(request);
		}
		return callback;
	}

	private void checkNotPrimitive(Type type) {
		if (type instanceof Class<?> && ((Class<?>) type).isPrimitive()
				&& type != void.class) {
			// TODO: Refer to the method/mapper class?
			throw new DataException(
					"Going to return null result, but return type is primitive ("
							+ type + ")");
		}
	}

	private Object selectKey(Object result, StatementConfig stConfig,
			boolean after, Object[] args) {
		if (stConfig.isInsert() && stConfig.getSelectKey() != null
				&& after == stConfig.isSelectKeyAfter()) {
			// TODO: Logging
			result = query(stConfig.getSelectKey(), args);
			if (stConfig.getKeySetter() != null) {
				stConfig.getKeySetter().setValue(args, result);
			}
		}
		return result;
	}

	private void mapParameters(PreparedStatement statement, List<Type> types,
			List<Object> params) throws SQLException {

		TypeHandlerFactory factory = config.getTypeHandlerFactory();

		for (int i = 0; i < params.size(); ++i) {
			Object item = params.get(i);
			Type type = types.get(i);
			TypeHandler<?> typeHandler = factory.getTypeHandler(type);
			typeHandler.setParameter(statement, i + 1, item);
		}
	}

	private static class ResultGetterSetter implements Getter, Setter {

		private Type type;

		/**
		 * Constructor.
		 * 
		 * @param type result map result type.
		 */
		public ResultGetterSetter(Type type) {
			this.type = type;
		}

		/**
		 * {@inheritDoc}
		 */
		public Type getType() {
			return type;
		}

		/**
		 * {@inheritDoc}
		 */
		public Object getValue(Object instance) {
			Request request = (Request) instance;
			return request.getResult();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setValue(Object instance, Object value) {
			Request request = (Request) instance;
			request.setResult(value);
		}
	}

	// TODO: toString

	/**
	 * {@link Session} implementation.
	 */
	private class TransactionImpl implements Session {

		final private SessionSpi spi;

		/**
		 * Constructor.
		 * 
		 * @param spi {@link SessionSpi} implementation.
		 */
		TransactionImpl(SessionSpi spi) {
			this.spi = spi;
		}

		/**
		 * {@inheritDoc}
		 */
		public void commit() {
			checkThread();
			spi.commit();
		}

		/**
		 * {@inheritDoc}
		 */
		public void end() {
			checkThread();

			// Remove from active sessions thread local
			sessions.remove();
			spi.end();
		}

		/**
		 * {@inheritDoc}
		 */
		public void rollback() {
			checkThread();
			spi.rollback();
		}

		private void checkThread() {
			if (sessions.get() != spi) {
				throw new IllegalStateException(
						"This transaction is not bound to this thread!");
			}
		}
	}
}
