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
package com.google.code.nanorm.internal.mapping.result;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.Setter;

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 05.06.2008
 */
public class ResultCollectorUtil {
    
    public static ResultCallbackSource createResultCallback(Type type, 
            Getter getter, Setter setter, Object targetName) {
        if(type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            if(pt.getRawType() == List.class) {
                return new ArrayListCallbackSource(getter, setter);
            }
        } else {
            return new SingleResultCallbackSource(setter, targetName);
        }
        throw new RuntimeException("Unexpected type");
    }
    
    public static Class<?> resultClass(Type resultType) {
        Class<?> resultClass;
        if (resultType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) resultType;
            if (pt.getRawType() == List.class) {
                // TODO: Cast!
                // TODO: Move to utils?
                resultClass = (Class<?>) pt.getActualTypeArguments()[0];
            } else {
                throw new RuntimeException("Type not supported: " + pt);
            }
        } else if (resultType instanceof Class<?>) {
            resultClass = (Class<?>) resultType;
        } else {
            throw new RuntimeException("Type not supported: " + resultType);
        }
        return resultClass;
    }
}
