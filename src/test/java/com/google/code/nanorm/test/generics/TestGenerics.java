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

package com.google.code.nanorm.test.generics;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.junit.Assert;
import org.junit.Test;

import com.google.code.nanorm.internal.introspect.asm.ResolvedParameterizedType;
import com.google.code.nanorm.internal.introspect.asm.TypeOracle;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 20.06.2008
 */
public class TestGenerics {

    @Test
    public void testSome() throws Exception {
        ParameterizedType pt = new ResolvedParameterizedType(Owner.class);
        String[] path = "getItem.getValue.getValue.getModel".split("\\.");
        // String[] path = "getItem2.getValue.getValue.getModel".split("\\.");
        for (int i = 0; i < path.length; ++i) {
            // TODO: Check!
            Class<?> clazz = (Class<?>) pt.getRawType();
            Method m = clazz.getMethod(path[i]);
            Type t = m.getGenericReturnType();
            pt = TypeOracle.resolve(t, pt);
        }
    }

    /**
     * @see TypeOracle
     * @throws Exception
     */
    @Test
    public void testSample() throws Exception {
        // Code to resolve getValue actual type
        Class<?> clazz = Bean.class;
        Type returnType = clazz.getMethod("getItem").getGenericReturnType();
        ParameterizedType pt = new ResolvedParameterizedType(clazz);
         
        // Resolve return type of getItem method
        pt = TypeOracle.resolve(returnType, pt);
        
        // Resolve return type of getValue method
        returnType = Wrapper.class.getMethod("getValue").getGenericReturnType();
        pt = TypeOracle.resolve(returnType, pt);
        
        Assert.assertEquals(String.class, pt.getRawType());
    }
}
