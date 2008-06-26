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
package com.google.code.nanorm.internal.type;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.code.nanorm.TypeHandlerFactory;

/**
 * Type handler factory implementation.
 * 
 * @see TypeHandlerFactory
 * @author Ivan Dubrov
 * @version 1.0 31.05.2008
 */
public final class TypeHandlerFactoryImpl implements TypeHandlerFactory {
    
    private final Map<Type, TypeHandler<?>> typeHandlers = new HashMap<Type, TypeHandler<?>>();
    
    private final UnknownTypeHandler unknownTypeHandler = new UnknownTypeHandler();
    
    /**
     * Constructor. Registers default type handlers.
     */
    public TypeHandlerFactoryImpl() {
        register(byte.class, new ByteTypeHandler());
        register(Byte.class, new ByteTypeHandler());
        
        register(short.class, new ShortTypeHandler());
        register(Short.class, new ShortTypeHandler());

        register(int.class, new IntTypeHandler());
        register(Integer.class, new IntTypeHandler());

        register(long.class, new LongTypeHandler());
        register(Long.class, new LongTypeHandler());

        register(boolean.class, new BooleanTypeHandler());
        register(Boolean.class, new BooleanTypeHandler());
        
        register(char.class, new CharTypeHandler());
        register(Character.class, new CharTypeHandler());

        register(float.class, new FloatTypeHandler());
        register(Float.class, new FloatTypeHandler());

        register(double.class, new DoubleTypeHandler());
        register(Double.class, new DoubleTypeHandler());
        
        register(String.class, new StringTypeHandler());
    }

    /**
     * {@inheritDoc}
     */
    public TypeHandler<?> getTypeHandler(Type type) {
        TypeHandler<?> typeHandler = typeHandlers.get(type);
        if(typeHandler == null) {
            typeHandler = unknownTypeHandler;
        }
        return typeHandler;
    }

    /**
     * {@inheritDoc}
     */
    public TypeHandler<Object> getUnknownTypeHandler() {
        return unknownTypeHandler;
    }
    
    /**
     * {@inheritDoc}
     */
    public final void register(Type type, TypeHandler<?> handler) {
        typeHandlers.put(type, handler);
    }
}
