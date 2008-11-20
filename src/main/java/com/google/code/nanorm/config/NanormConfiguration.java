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

package com.google.code.nanorm.config;

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.nanorm.NanormFactory;
import com.google.code.nanorm.TypeHandler;
import com.google.code.nanorm.TypeHandlerFactory;
import com.google.code.nanorm.exceptions.ConfigurationException;
import com.google.code.nanorm.internal.FactoryImpl;
import com.google.code.nanorm.internal.config.InternalConfiguration;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;
import com.google.code.nanorm.internal.introspect.asm.ASMIntrospectionFactory;
import com.google.code.nanorm.internal.introspect.reflect.ReflectIntrospectionFactory;
import com.google.code.nanorm.internal.type.TypeHandlerFactoryImpl;

/**
 * Public configuration class for nanorm factory creation.
 * 
 * Configuration instances are not thread-safe. You should not invoke methods on
 * it from different threads.
 * 
 * @author Ivan Dubrov
 * @version 1.0 19.06.2008
 */
public class NanormConfiguration {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NanormConfiguration.class);

	private final TypeHandlerFactory typeHandlerFactory;

	private final IntrospectionFactory introspectionFactory;

	private final InternalConfiguration config;
	
	private boolean autoSessionEnabled = false;

	private SessionConfig sessionConfig;

	/**
	 * Constructor.
	 */
	public NanormConfiguration() {
		typeHandlerFactory = new TypeHandlerFactoryImpl();
		introspectionFactory = detectFactory();
		config = new InternalConfiguration(typeHandlerFactory, introspectionFactory);
	}
	
	/**
	 * Add given mapper interface to the configuration.
	 * 
	 * @param mapperClasses mapper classes
	 * @throws ConfigurationException configuration is invalid
	 */
	public void configure(Class<?>... mapperClasses) throws ConfigurationException {
		for(Class<?> mapperClass : mapperClasses) {
			config.configure(mapperClass);
		}
	}

	/**
	 * Register type handler.
	 * 
	 * @param type type for handler
	 * @param handler type handler
	 */
	public void registerTypeHandler(Type type, TypeHandler<?> handler) {
		typeHandlerFactory.register(type, handler);
	}

	/**
	 * Set session/transaction management configuration.
	 * 
	 * @param sessionConfig session/transaction management configuration.
	 */
	public void setSessionConfig(SessionConfig sessionConfig) {
		this.sessionConfig = sessionConfig;
	}
	
	/**
	 * <p>
	 * Enable or disable auto-session feature. With this feature set to true,
	 * if session was not created when executing the query, it will be automatically
	 * created and closed when query finishes. If auto-session is disabled, factory
	 * will throw an exception if query is executed and session is not explicitly opened
	 * for current thread beforeahead. 
	 * </p>
	 * <p>
	 * Auto-session is off by default (to force user to create session).
	 * </p>
	 * 
	 * @param autoSessionEnabled true to enable auto-session, false to disable
	 */
	public void setAutoSessionEnabled(boolean autoSessionEnabled) {
		this.autoSessionEnabled = autoSessionEnabled;
	}

	/**
	 * Build factory.
	 * 
	 * @return factory.
	 */
	public NanormFactory buildFactory() {
		if(autoSessionEnabled && sessionConfig == null) {
			throw new IllegalArgumentException("Auto-session feature requires sessionConfig to be set");
		}

		return new FactoryImpl(config, sessionConfig, autoSessionEnabled);
	}
	
	private IntrospectionFactory detectFactory() {
		try {
			// Trying to detect the ASM library
			Class.forName("org.objectweb.asm.ClassVisitor");
			
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			return new ASMIntrospectionFactory(cl);
		} catch(ClassNotFoundException e) {
			LOGGER.info("ASM library not found in classpath, using reflection (slower).");
			return new ReflectIntrospectionFactory();
		}
	}
}
