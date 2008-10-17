package com.google.code.nanorm.internal.config;

import java.lang.reflect.Type;

import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.Setter;

/**
 * Parameter mapping configuration.
 * 
 * @author Ivan Dubrov
 */
public final class ParameterMappingConfig {

	private final Type type;

	private final Getter getter;

	private final Setter setter;

	/**
	 * Constructor.
	 * 
	 * @param type parameter type
	 * @param getter getter for retrieving parameter value from array of arguments
	 * @param setter setter for setting parameter value
	 */
	public ParameterMappingConfig(Type type, Getter getter, Setter setter) {
		this.type = type;
		this.getter = getter;
		this.setter = setter;
	}

	/** @return the getter */
	public Getter getGetter() {
		return getter;
	}

	/** @return the type */
	public Type getType() {
		return type;
	}

	/** @return the setter */
	public Setter getSetter() {
		return setter;
	}

	// TODO: toString
}
