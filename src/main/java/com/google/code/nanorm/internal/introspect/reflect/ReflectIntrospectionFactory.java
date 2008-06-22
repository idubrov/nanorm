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
package com.google.code.nanorm.internal.introspect.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.code.nanorm.internal.FactoryImpl;
import com.google.code.nanorm.internal.QueryDelegate;
import com.google.code.nanorm.internal.config.InternalConfiguration;
import com.google.code.nanorm.internal.config.StatementConfig;
import com.google.code.nanorm.internal.introspect.AbstractIntrospectionFactory;
import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.IntrospectUtils;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;
import com.google.code.nanorm.internal.introspect.Setter;
import com.google.code.nanorm.internal.introspect.asm.AccessorKey;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 04.06.2008
 */
public class ReflectIntrospectionFactory extends AbstractIntrospectionFactory implements
        IntrospectionFactory {
    
    final private Map<AccessorKey, Method> getters = new ConcurrentHashMap<AccessorKey, Method>();
    
    final private Map<AccessorKey, Method> setters = new ConcurrentHashMap<AccessorKey, Method>();

    /**
     * @see com.google.code.nanorm.internal.introspect.IntrospectionFactory#buildGetter(java.lang.Class,
     * java.lang.String)
     */
    public Getter buildGetter(Class<?> beanClass, String path) {
        return new ReflectGetter(this, beanClass, path);
    }

    /**
     * @see com.google.code.nanorm.internal.introspect.IntrospectionFactory#buildSetter(java.lang.Class,
     * java.lang.String)
     */
    public Setter buildSetter(Class<?> beanClass, String path) {
        return new ReflectSetter(this, path);
    }

    /**
     * @see com.google.code.nanorm.internal.introspect.IntrospectionFactory#buildParameterGetter(java.lang.String)
     */
    public Getter buildParameterGetter(Type[] types, String path) {
        return new ParameterGetter(this, types, path);
    }
    
    protected Method lookupGetter(Class<?> clazz, String property) {
        AccessorKey key = new AccessorKey(clazz, property);
        Method m = getters.get(key);
        if(m == null) {
            m = IntrospectUtils.findGetter(clazz, property);
            getters.put(key, m);
        }
        return m;
    }
    
    protected Method lookupSetter(Class<?> clazz, String property) {
        AccessorKey key = new AccessorKey(clazz, property);
        Method m = setters.get(key);
        if(m == null) {
            m = IntrospectUtils.findSetter(clazz, property);
            setters.put(key, m);
        }
        return m;
    }

    /**
     * {@inheritDoc}
     */
    public <T> T createMapper(Class<T> interfaze, InternalConfiguration config, QueryDelegate delegate) {
        return interfaze.cast(Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[] {interfaze }, new MapperInvocationHandler(config, delegate)));
    }
    
    /**
     * Invocation handler for mapper interface implementation.
     *
     * @author Ivan Dubrov
     * @version 1.0 19.06.2008
     */
    private class MapperInvocationHandler implements InvocationHandler {
        private final InternalConfiguration config;
        
        private final QueryDelegate delegate;
        
        /**
         * 
         */
        public MapperInvocationHandler(InternalConfiguration config, QueryDelegate delegate) {
            this.config = config;
            this.delegate = delegate;
        }

        /**
         * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
         * java.lang.reflect.Method, java.lang.Object[])
         */
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            StatementConfig stConfig = config.getStatementConfig(method);
            return delegate.query(stConfig, args);
        }
    }
}
