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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.google.code.nanorm.Factory;
import com.google.code.nanorm.internal.config.Configuration;
import com.google.code.nanorm.internal.config.StatementConfig;
import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.Setter;
import com.google.code.nanorm.internal.mapping.result.ResultCallback;
import com.google.code.nanorm.internal.mapping.result.ResultCallbackSource;
import com.google.code.nanorm.internal.mapping.result.ResultCollectorUtil;
import com.google.code.nanorm.internal.mapping.result.ResultMap;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
public class FactoryImpl implements Factory {

    final private ThreadLocal<Connection> conn = new ThreadLocal<Connection>();

    final private Configuration config;
    
    /**
     * 
     */
    public FactoryImpl() {
        config = new Configuration();
    }
    
    /**
     * @see com.google.code.nanorm.Factory#createMapper(java.lang.Class)
     */
    public <T> T createMapper(Class<T> mapperClass) {
        config.configure(mapperClass);
        
        // TODO: Check we mapped this class!
        return mapperClass.cast(Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[] {mapperClass }, new MapperInvocationHandler()));
    }

    public void useConnection(Connection connection) {
        this.conn.set(connection);
    }
   
    protected Object query(StatementConfig config, Object[] args) throws SQLException {
        // Request-scoped data
        Request request = new Request();
        
        // Statement fragment
        BoundFragment fragment = config.getStatementBuilder().bindParameters(args);
        
        // SQL, parameters and their types
        StringBuilder sql = new StringBuilder();
        List<Object> parameters = new ArrayList<Object>();
        List<Type> types = new ArrayList<Type>();
        
        // Generate everything
        fragment.generate(sql, parameters, types);
        
        PreparedStatement st = this.conn.get().prepareStatement(sql.toString());
        try {
            // Map parameters and execute query
            mapParameters(st, types, parameters);
            ResultSet rs = st.executeQuery();
        
            // Prepare result callback and process results
            ResultGetterSetter rgs = new ResultGetterSetter();
            ResultCallbackSource callbackSource = ResultCollectorUtil.createResultCallback(
                    config.getResultType(), rgs, rgs);
            ResultCallback callback = callbackSource.forInstance(request);            
            ResultMap resultMapper = config.getResultMapper();
            while(rs.next()) {
                resultMapper.processResultSet(request, rs, callback);
            }
            return request.getResult();
        } finally {
            st.close();
        }
    }
    
    protected void mapParameters(PreparedStatement statement, List<Type> types,
            List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); ++i) {
            Object item = params.get(i);
            Type type = types.get(i);
            if (type == Integer.class || type == int.class) {
                if (item != null) {
                    statement.setInt(i + 1, (Integer) item);
                } else {
                    statement.setNull(i + 1, Types.INTEGER);
                }
            } else if (type == String.class) {
                if (item != null) {
                    statement.setString(i + 1, (String) item);
                } else {
                    statement.setNull(i + 1, Types.VARCHAR);
                }
            } else {
                throw new RuntimeException("TYPE " + type + " NOT SUPPORTED");
            }
        }
    }
    
    public class MapperInvocationHandler implements InvocationHandler {

        /**
         * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
         * java.lang.reflect.Method, java.lang.Object[])
         */
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            StatementConfig stConfig = config.getStatementConfig(method);
            return query(stConfig, args);
        }
    }
    
    private static class ResultGetterSetter implements Getter, Setter {
        /**
         * {@inheritDoc}
         */
        public Type getType() {
            // TODO: Implement somehow!
            return null;
        }
        
        /**
         * @see com.google.code.nanorm.internal.introspect.Getter#getValue(java.lang.Object[])
         */
        public Object getValue(Object instance) {
            Request request = (Request) instance;
            return request.getResult();
        }

        /**
         * @see com.google.code.nanorm.internal.introspect.Setter#setValue(java.lang.Object, java.lang.Object)
         */
        public void setValue(Object instance, Object value) {
            Request request = (Request) instance;
            request.setResult(value);
        }
    }
    
    // TODO: toString
}
