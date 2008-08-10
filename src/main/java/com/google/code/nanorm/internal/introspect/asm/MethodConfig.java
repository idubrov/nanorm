package com.google.code.nanorm.internal.introspect.asm;

import java.lang.reflect.Method;

/**
 * Method configuration for mapper builder.
 * 
 * @author Ivan Dubrov
 */
class MethodConfig {
	/**
	 * Index in the array of statement configurations which are passed to the mapper
	 * constructor.
	 */
	private int index;

	/**
	 * Mapper method.
	 */
	private Method method;
	
	/**
	 * Constructor.
	 * @param index index
	 * @param method method
	 */
	public MethodConfig(Method method, int index) {
		this.index = index;
		this.method = method;
	}
	
	/**
	 * Getter for method.
	 * @return index
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Getter for method.
	 * @return method.
	 */
	public Method getMethod() {
		return method;
	}
}