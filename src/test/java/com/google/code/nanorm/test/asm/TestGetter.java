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

package com.google.code.nanorm.test.asm;

import java.lang.reflect.Type;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;
import com.google.code.nanorm.internal.introspect.Setter;
import com.google.code.nanorm.internal.introspect.asm.ASMIntrospectionFactory;
import com.google.code.nanorm.test.beans.Car;
import com.google.code.nanorm.test.beans.Owner;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 19.06.2008
 */
public class TestGetter {

    private static IntrospectionFactory factory;

    @BeforeClass
    public static void setUp() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        factory = new ASMIntrospectionFactory(cl);
    }

    @Test
    public void testGetter() {
        Owner owner = new Owner();
        owner.setFirstName("Ivan");
        Car car = new Car();
        car.setOwner(owner);
        Getter getter = factory.buildGetter(Car.class, "owner.firstName");

        Assert.assertEquals("Ivan", getter.getValue(car));
        
        Assert.assertEquals(String.class, getter.getType());
    }

    @Test
    public void testGetterNPE() {
        Car car = new Car();
        car.setOwner(null);
        Getter getter = factory.buildGetter(Car.class, "owner.firstName");

        try {
            getter.getValue(car);
        } catch (NullPointerException e) {
            Assert.assertEquals(
                    "owner property is null for com.google.code.nanorm.test.beans.Car instance.",
                    e.getMessage());
        }
    }
    
    @Test
    public void testSetter() {
        Owner owner = new Owner();
        owner.setFirstName("John");
        Car car = new Car();
        car.setOwner(owner);
        Setter setter = factory.buildSetter(Car.class, "owner.firstName");

        setter.setValue(car, "Ivan");

        Assert.assertEquals("Ivan", car.getOwner().getFirstName());
    }
    
    @Test
    public void testParameterGetter() {
        Owner owner = new Owner();
        owner.setFirstName("Ivan");
        Car car = new Car();
        car.setOwner(owner);
        Car[] arr = new Car[4];
        arr[1] = car;
        Type[] types = new Type[] { Car.class, Car.class, Car.class, Car.class }; 
        Getter getter = factory.buildParameterGetter(types, "2.owner.firstName");

        Assert.assertEquals("Ivan", getter.getValue(arr));
        
        Assert.assertEquals(String.class, getter.getType());
    }
    
    @Test
    public void testParameterGetter2() {
        String[] arr = new String[] { "one", "two", "three", "four" };
        Type[] types = new Type[] { String.class, String.class, String.class, String.class }; 
        Getter getter = factory.buildParameterGetter(types, "2");
        Assert.assertEquals("two", getter.getValue(arr));
        Assert.assertEquals(String.class, getter.getType());
    }
}

