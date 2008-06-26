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
import com.google.code.nanorm.internal.introspect.TypeOracle;
import com.google.code.nanorm.internal.introspect.asm.ASMIntrospectionFactory;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 20.06.2008
 */
@SuppressWarnings("all")
public class TestGenerics {
    
    private static IntrospectionFactory factory;
    
    @BeforeClass
    public static void setUp() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        factory = new ASMIntrospectionFactory(loader);
    }
    
    @Test
    public void testGenericTypes() throws Exception {
        Assert.assertEquals(String.class, factory.getPropertyType(Container.class, "item.value.value.name"));
        Assert.assertEquals(String.class, factory.getPropertyType(Container.class, "item2.value.value.name"));
        Assert.assertEquals(String.class, factory.getPropertyType(Container.class, "item3.value.name"));
        Assert.assertEquals(String.class, factory.getPropertyType(Container.class, "item5.value[0].name"));
        Assert.assertEquals(String.class, factory.getPropertyType(Container.class, "item6.value[0].name"));
    }
    
    @Test
    public void testGenericAccess() throws Exception {
        Container owner = new Container();
        owner.setItem(new Wrapper<Wrapper<Thing>>());
        owner.getItem().setValue(new Wrapper<Thing>());
        owner.getItem().getValue().setValue(new Thing());
        
        // Test
        owner.getItem().getValue().getValue().setName("Lada");
        Getter getter = factory.buildGetter(Container.class, "item.value.value.name");
        Assert.assertEquals("Lada", getter.getValue(owner));
        
        Setter setter = factory.buildSetter(Container.class, "item.value.value.name");
        setter.setValue(owner, "Kalina");
        Assert.assertEquals("Kalina", owner.getItem().getValue().getValue().getName());
    }
    
    @Test
    public void testGenericAccess2() throws Exception {
        Container owner = new Container();
        owner.setItem2(new Wrapper2<Thing>());
        owner.getItem2().setValue(new Wrapper<Thing>());
        owner.getItem2().getValue().setValue(new Thing());
        
        // Test
        owner.getItem2().getValue().getValue().setName("Lada");
        Getter getter = factory.buildGetter(Container.class, "item2.value.value.name");
        Assert.assertEquals("Lada", getter.getValue(owner));
        
        Setter setter = factory.buildSetter(Container.class, "item2.value.value.name");
        setter.setValue(owner, "Kalina");
        Assert.assertEquals("Kalina", owner.getItem2().getValue().getValue().getName());
        
        
    }

    @Test
    public void testGenericAccess3() throws Exception {
        Container owner = new Container();
        Wrapper<Thing> w = new Wrapper<Thing>();
        owner.setItem3(w);
        w.setValue(new Thing());
        
        // Test
        owner.getItem3().getValue().setName("Lada");
        Getter getter = factory.buildGetter(Container.class, "item3.value.name");
        Assert.assertEquals("Lada", getter.getValue(owner));
        
        Setter setter = factory.buildSetter(Container.class, "item3.value.name");
        setter.setValue(owner, "Kalina");
        Assert.assertEquals("Kalina", owner.getItem3().getValue().getName());
    }
    
    @Test
    public void testGenericAccess5() throws Exception {
        Container owner = new Container();
        Wrapper3<Thing> w = new Wrapper3<Thing>();
        w.setValue(new Thing[10]);
        w.getValue()[4] = new Thing();
        owner.setItem5(w);
        
        // Test
        owner.getItem5().getValue()[4].setName("Something");
        Getter getter = factory.buildGetter(Container.class, "item5.value[4].name");
        Assert.assertEquals("Something", getter.getValue(owner));
        
        Setter setter = factory.buildSetter(Container.class, "item5.value[4].name");
        setter.setValue(owner, "Different");
        Assert.assertEquals("Different", owner.getItem5().getValue()[4].getName());
    }
    
    @Test
    public void testGenericAccess6() throws Exception {
        Container owner = new Container();
        Wrapper<Thing[]> w = new Wrapper<Thing[]>();
        w.setValue(new Thing[10]);
        w.getValue()[4] = new Thing();
        owner.setItem6(w);
        
        // Test
        owner.getItem6().getValue()[4].setName("Something");
        Getter getter = factory.buildGetter(Container.class, "item6.value[4].name");
        Assert.assertEquals("Something", getter.getValue(owner));
        
        Setter setter = factory.buildSetter(Container.class, "item6.value[4].name");
        setter.setValue(owner, "Different");
        Assert.assertEquals("Different", owner.getItem6().getValue()[4].getName());
    }

    
    @Test
    public void testSome() throws Exception {
        Type type = Container.class;
        String[] path = "getItem.getValue.getValue.getName".split("\\.");
        // String[] path = "getItem2.getValue.getValue.getModel".split("\\.");
        for (int i = 0; i < path.length; ++i) {
            // TODO: Check!
            Class<?> clazz = TypeOracle.resolveClass(type);
            Method m = clazz.getMethod(path[i]);
            type = TypeOracle.resolve(m.getGenericReturnType(), type);
        }
    }

    /**
     * @see TypeOracle
     * @throws Exception exception
     */
    @Test
    public void testSample() throws Exception {
        // Code to resolve getValue actual type
        Class<?> clazz = SampleBean.class;
        Type returnType = clazz.getMethod("getItem").getGenericReturnType();
        Type type = clazz;
         
        // Resolve return type of getItem method
        type = TypeOracle.resolve(returnType, type);
        
        // Resolve return type of getValue method
        returnType = Wrapper.class.getMethod("getValue").getGenericReturnType();
        type = TypeOracle.resolve(returnType, type);
        
        Assert.assertEquals(String.class, TypeOracle.resolveClass(type));
    }
}
