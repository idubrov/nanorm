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
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.nanorm.DataSink;
import com.google.code.nanorm.NanormFactory;
import com.google.code.nanorm.Session;
import com.google.code.nanorm.annotations.Options;
import com.google.code.nanorm.annotations.SelectKeyType;
import com.google.code.nanorm.config.SessionConfig;
import com.google.code.nanorm.exceptions.ConfigurationException;
import com.google.code.nanorm.exceptions.DataException;
import com.google.code.nanorm.internal.config.InternalConfiguration;
import com.google.code.nanorm.internal.config.QueryKind;
import com.google.code.nanorm.internal.config.StatementConfig;
import com.google.code.nanorm.internal.config.StatementKey;
import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.Setter;
import com.google.code.nanorm.internal.mapping.parameter.ParameterMapper;
import com.google.code.nanorm.internal.mapping.result.DataSinkSource;
import com.google.code.nanorm.internal.mapping.result.ResultCollectorUtil;
import com.google.code.nanorm.internal.mapping.result.RowMapper;
import com.google.code.nanorm.internal.session.SessionSpi;
import com.google.code.nanorm.internal.session.SingleConnSessionSpi;
import com.google.code.nanorm.internal.util.Messages;

/**
 * Factory implementation.
 * 
 * Executing the query and iteration through result set is located here.
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

    private final boolean autoSessionEnabled;

    /**
     * Logger for logging the SQL statements.
     */
    private static final Logger LOGGER_SQL = LoggerFactory.getLogger(FactoryImpl.class
            .getPackage().getName()
            + ".SQL");

    /**
     * Logger for logging all other events.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FactoryImpl.class.getName());

    /**
     * Constructor.
     * 
     * @param internalConfig factory configuration
     * @param sessionConfig session configuration
     * @param autoSessionEnabled true if autosession feature is enabled (session
     * is created automatically when query is executed). Otherwise, an exception
     * is thrown.
     */
    public FactoryImpl(InternalConfiguration internalConfig, SessionConfig sessionConfig,
            boolean autoSessionEnabled) {
        this.config = internalConfig;
        this.sessionSpiConfig = sessionConfig;
        this.autoSessionEnabled = autoSessionEnabled;
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
    public <T> T createMapper(Class<T> mapperClass) throws ConfigurationException {
        config.configure(mapperClass);

        // TODO: Check we mapped this class and return existing one!
        return config.getIntrospectionFactory().createMapper(mapperClass, config, this);
    }

    /**
     * {@inheritDoc}
     */
    public Session openSession() {
        if (sessionSpiConfig == null) {
            throw new IllegalArgumentException("Session SPI is not configured!");
        }
        if (sessions.get() != null) {
            throw new IllegalStateException("Session was already started for this thread!");
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
            throw new IllegalStateException("Session was already started for this thread!");
        }

        final SessionSpi spi = new SingleConnSessionSpi(connection);
        sessions.set(spi);
        return new TransactionImpl(spi);
    }

    /**
     * {@inheritDoc}
     */
    public Object query(StatementConfig stConfig, Object[] args) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Executing the query " + stConfig.getId());
        }
        // Request-scoped data
        Request request = new Request(this);

        SessionSpi spi = sessions.get();
        boolean isAuto = false;
        if (spi == null) {
            if (!autoSessionEnabled) {
                throw new IllegalStateException("Session is not opened for current thread, "
                        + "but auto-session is disabled for this factory.");
            }
            // Auto-create session for single request
            isAuto = true;
            spi = sessionSpiConfig.newSessionSpi();
        }

        // Close session spi after this block if in auto mode
        try {
            // Generate key prior to mapping the parameters, so we
            // have a chance to update arguments with generated key
            selectKey(request, stConfig, false, args);

            // Bind fragment to arguments
            BoundFragment fragment = stConfig.getStatementBuilder().bindParameters(args);

            // SQL, parameters and their types
            StringBuilder sql = new StringBuilder();
            List<ParameterMapper> parameters = new ArrayList<ParameterMapper>();

            // Fill SQL string builder and parameter mappers
            fragment.generate(sql, parameters);

            // Close connection after this try
            Connection conn = spi.getConnection();
            try {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Using the connection " + conn);
                }

                if (LOGGER_SQL.isDebugEnabled()) {
                    LOGGER_SQL.debug(sql.toString());
                    if (LOGGER_SQL.isTraceEnabled()) {
                        LOGGER_SQL.trace("Parameters: " + parameters.toString());
                    }
                }

                // Should we get key using JDBC getGeneratedKeys
                boolean isJDBCKey = stConfig.getSelectKeyType() == SelectKeyType.AFTER
                        && stConfig.getSelectKey().getStatementBuilder() == null;

                // Prepare the statement
                PreparedStatement st;
                if (stConfig.getKind() == QueryKind.CALL) {
                    st = conn.prepareCall(sql.toString());

                    // Register OUT parameters
                    CallableStatement cs = (CallableStatement) st;
                    for (ParameterMapper mapper : parameters) {
                        // TODO: Void.class handling (null parameters)
                        mapper.registerOutParameter(config.getTypeHandlerFactory(), cs);
                    }
                } else {
                    st = isJDBCKey ? conn.prepareStatement(sql.toString(),
                            Statement.RETURN_GENERATED_KEYS) : conn.prepareStatement(sql
                            .toString());
                }

                // Set the options on the prepared statement
                updateOptions(st, stConfig.getOptions());

                // Close statement after this try
                try {
                    // Map parameters to the statement
                    for (ParameterMapper mapper : parameters) {
                        // TODO: Void.class handling (null parameters)
                        mapper.mapParameterIn(config.getTypeHandlerFactory(), st);
                    }

                    if (stConfig.getKind() == QueryKind.INSERT) {
                        st.executeUpdate();

                        if (isJDBCKey) {
                            // If we use getGeneratedKeys, we need to process
                            // the result set with generated keys as regular
                            // result set
                            processResultSet(stConfig.getSelectKey(), args, request, st
                                    .getGeneratedKeys());

                            // Set the property
                            if (stConfig.getKeySetter() != null) {
                                stConfig.getKeySetter().setValue(args, request.getResult());
                            }
                        } else {
                            // otherwise, simply execute statement that selects
                            // a key
                            selectKey(request, stConfig, true, args);
                        }
                    } else if (stConfig.getKind() == QueryKind.UPDATE) {
                        // The result is amount of rows updated
                        request.setResult(st.executeUpdate());
                    } else {
                        processResultSet(stConfig, args, request, st.executeQuery());
                    }

                    // Map OUT parameters
                    if (stConfig.getKind() == QueryKind.CALL) {
                        CallableStatement cs = (CallableStatement) st;
                        for (ParameterMapper mapper : parameters) {
                            // TODO: Void.class handling (null parameters)
                            mapper.mapParameterOut(config.getTypeHandlerFactory(), cs);
                        }
                    }
                } finally {
                    st.close();
                }
            } catch (SQLException e) {
                throw new DataException("SQL exception occured while executing the query!", e);
            } finally {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Releasing the connection " + conn);
                }
                try {
                    spi.releaseConnection(conn);
                } catch (DataException e) {
                    LOGGER.error("Failed to release the connection.", e);
                }
            }
        } finally {
            if (isAuto) {
                spi.end();
            }
        }
        if (request.getResult() == null) {
            checkNotPrimitive(stConfig);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query result is " + request.getResult());
        }
        return request.getResult();
    }

    /**
     * Update options on the prepared statement.
     * @param st
     * @param config
     * @throws SQLException option value is invalid
     */
    private void updateOptions(PreparedStatement st, Options opts) throws SQLException {
        if (opts != null) {
            st.setFetchSize(opts.fetchSize());
        }
    }

    /**
     * Process the result set. Iterate through the rows and map the data to the beans.
     * 
     * @param stConfig statement config
     * @param args statement arguments
     * @param request request reference
     * @param rs result set
     * @throws SQLException
     */
    private void processResultSet(StatementConfig stConfig, Object[] args, Request request,
            ResultSet rs) throws SQLException {

        try {
            // Create callback that will receive the mapped objects
            DataSink<Object> callback = createResultSink(stConfig, args, request);

            // Iterate through the result set
            RowMapper rowMapper = stConfig.getRowMapper();
            while (rs.next()) {
                rowMapper.processResultSet(request, rs, callback);
            }
            callback.commitData();

            // Commit all callbacks used in the request
            request.commitCallbacks();
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.error("Failed to close ResultSet", e);
            }
        }
    }

    private DataSink<Object> createResultSink(StatementConfig stConfig, Object[] args,
            Request request) {

        // If we have DataSink in mapper method parameters -- use it,
        // otherwise create callback which will set result to the request.
        DataSink<Object> sink;
        if (stConfig.getCallbackIndex() != StatementConfig.RETURN_VALUE) {
            // This is OK, since we deduced result type exactly
            // from this parameter
            @SuppressWarnings("unchecked")
            DataSink<Object> temp = (DataSink<Object>) args[stConfig.getCallbackIndex()];
            sink = temp;
        } else {
            // Prepare data sink and process results
            ResultGetterSetter rgs = new ResultGetterSetter(stConfig.getResultType());
            DataSinkSource sinkSource = ResultCollectorUtil.createDataSinkSource(rgs, rgs,
                    stConfig);

            sink = sinkSource.forInstance(request);
        }
        return sink;
    }

    /**
     * Check statement return type is not primitive. Throws exception otherwise.
     * @param stConfig statement config
     */
    private void checkNotPrimitive(StatementConfig stConfig) {
        Type type = stConfig.getResultType();

        if (type instanceof Class<?> && ((Class<?>) type).isPrimitive() && type != void.class) {

            StatementKey key = stConfig.getId();
            throw new DataException(Messages.nullResult(key.getMapper(), key.getName(),
                    (Class<?>) type));
        }
    }

    /**
     * Execute the key selection query. Only queries if key type matches the
     * "after" parameter (in other words, the key is selected if key type is
     * AFTER and "after" is true or if key type is BEFORE and "after" is false).
     * @param request request reference
     * @param stConfig main statement config
     * @param after flag indicating if this invocation made before main query or after
     * @param args main query parameters 
     */
    private void selectKey(Request request, StatementConfig stConfig, boolean after, Object[] args) {
        boolean isKeyAfter = stConfig.getSelectKeyType() == SelectKeyType.AFTER;

        if (stConfig.getKind() == QueryKind.INSERT && stConfig.getSelectKey() != null
                && after == isKeyAfter) {

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Generating the key for statement " + stConfig.getId());
            }
            Object result = query(stConfig.getSelectKey(), args);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Generated key is " + result);
            }
            if (stConfig.getKeySetter() != null) {
                stConfig.getKeySetter().setValue(args, result);
            }

            request.setResult(result);
        }
    }

    private static class ResultGetterSetter implements Getter, Setter {

        private final Type type;

        /**
         * Constructor.
         * 
         * @param type result map result type.
         */
        private ResultGetterSetter(Type type) {
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

    /**
     * {@link Session} implementation.
     */
    private class TransactionImpl implements Session {

        private final SessionSpi spi;

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
                throw new IllegalStateException("This transaction is not bound to this thread!");
            }
        }
    }
}
