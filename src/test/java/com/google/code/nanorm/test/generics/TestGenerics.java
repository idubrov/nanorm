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
import java.lang.reflect.Type;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;
import com.google.code.nanorm.internal.introspect.Setter;
import com.google.code.nanorm.internal.introspect.asm.ASMIntrospectionFactory;
import com.google.code.nanorm.internal.introspect.asm.TypeOracle;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 20.06.2008
 */
public class TestGenerics {
    
    private static IntrospectionFactory factory;
    
    @BeforeClass
    public static void setUp() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        factory = new ASMIntrospectionFactory(loader);
    }
    
    @Test
    public void testGenericAccess() throws Exception {
        Owner owner = new Owner();
        owner.getItem().setValue(new Wrapper<Car>());
        owner.getItem().getValue().setValue(new Car());
        owner.getItem().getValue().getValue().setModel("Lada");
        
        Getter getter = factory.buildGetter(Owner.class, "item.value.value.model");
        
        Assert.assertEquals("Lada", getter.getValue(owner));
        
        Setter setter = factory.buildSetter(Owner.class, "item.value.value.model");
        
        setter.setValue(owner, "Kalina");
        Assert.assertEquals("Kalina", owner.getItem().getValue().getValue().getModel());
    }

    @Test
    public void testSome() throws Exception {
        Type type = Owner.class;
        String[] path = "getItem.getValue.getValue.getModel".split("\\.");
        // String[] path = "getItem2.getValue.getValue.getModel".split("\\.");
        for (int i = 0; i < path.length; ++i) {
            // TODO: Check!
            Class<?> clazz = TypeOracle.resolveRawType(type);
            Method m = clazz.getMethod(path[i]);
            type = TypeOracle.resolve(m.getGenericReturnType(), type);
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
        Type type = clazz;
         
        // Resolve return type of getItem method
        type = TypeOracle.resolve(returnType, type);
        
        // Resolve return type of getValue method
        returnType = Wrapper.class.getMethod("getValue").getGenericReturnType();
        type = TypeOracle.resolve(returnType, type);
        
        Assert.assertEquals(String.class, TypeOracle.resolveRawType(type));
    }
}
