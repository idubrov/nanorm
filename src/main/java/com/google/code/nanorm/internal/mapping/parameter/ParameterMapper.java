package com.google.code.nanorm.internal.mapping.parameter;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.google.code.nanorm.TypeHandlerFactory;
import com.google.code.nanorm.internal.config.ParameterMappingConfig;
import com.google.code.nanorm.internal.type.TypeHandler;

/**
 * Parameter mapper. Maps IN/OUT parmaters to {@link PreparedStatement}/
 * {@link CallableStatement}.
 * 
 * @author Ivan Dubrov
 */
public final class ParameterMapper {

	private final int index;

	private final Object[] args;

	private final ParameterMappingConfig config;

	/**
	 * Constructor.
	 * 
	 * @param config parameter mapping configuration
	 * @param index parameter index
	 * @param args argument to bind to
	 */
	public ParameterMapper(ParameterMappingConfig config, int index, Object[] args) {
		this.config = config;
		this.index = index;
		this.args = args;
	}

	/**
	 * Map IN parameter to {@link PreparedStatement}.
	 * 
	 * @param factory type handler factory
	 * @param ps prepared statement to set parameters to
	 * @throws SQLException exception while setting parameter
	 */
	public void mapParameterIn(TypeHandlerFactory factory, PreparedStatement ps)
			throws SQLException {
		// IN parameter
		if (config.getGetter() != null) {
			TypeHandler<?> typeHandler = factory.getTypeHandler(config.getType());

			Object value = config.getGetter().getValue(args);
			typeHandler.setParameter(ps, index, value);
		}
	}

	/**
	 * TODO: Not used by now
	 * TODO: Check out is used only for @Call
	 * 
	 * Map OUT parameter from {@link CallableStatement}.
	 * 
	 * @param factory type handler factory
	 * @param cs callable statement to get parameter from
	 * @throws SQLException exception while getting value
	 */
	public void mapParameterOut(TypeHandlerFactory factory, CallableStatement cs)
			throws SQLException {
		// OUT parameter
		if (config.getSetter() != null) {
			// TODO: Use typeHandler
			//TypeHandler<?> typeHandler = factory.getTypeHandler(config.getType());

			Object value = cs.getObject(index); // TODO: Use type handler;
			config.getSetter().setValue(args, value);
		}
	}
}
