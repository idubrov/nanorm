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

package com.google.code.nanorm.test;

import org.junit.Assert;
import org.junit.Test;

import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;
import com.google.code.nanorm.internal.introspect.Setter;
import com.google.code.nanorm.internal.introspect.asm.ASMIntrospectionFactory;
import com.google.code.nanorm.test.beans.Car;

/**
 * ASM introspection factory test. Extends base class which contains all tests
 * that are not specific to concrete factory.
 * 
 * @author Ivan Dubrov
 * @version 1.0 22.06.2008
 */
public class TestGetterASM extends TestGetterBase {

    protected IntrospectionFactory provideIntrospectionFactory() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return new ASMIntrospectionFactory(cl);
    }

    @Test
    public void testGetterNPE() {
        Car car = new Car();
        car.setOwner(null);
        Getter getter = factory.buildGetter(Car.class, "owner.firstName");

        try {
            getter.getValue(car);
        } catch (NullPointerException e) {
            Assert.assertEquals("owner property is null for "
                    + "com.google.code.nanorm.test.beans.Car "
                    + "instance (full path is owner.firstName).", e.getMessage());
        }
    }

    @Test
    public void testSetterNPE() {
        Car car = new Car();
        car.setOwner(null);
        Setter setter = factory.buildSetter(Car.class, "owner.firstName");

        try {
            setter.setValue(car, "Ivan");
        } catch (NullPointerException e) {
            Assert.assertEquals("owner property is null for "
                    + "com.google.code.nanorm.test.beans.Car "
                    + "instance (full path is owner.firstName).", e.getMessage());
        }
    }

    // TODO: This test, in fact, should work on Reflection-based factory!
    @Test
    public void testGetterArray3() {
        Car car = new Car();
        car.setModel("Kalina");
        Car[] cars = new Car[5];
        cars[3] = car;
            
        Getter getter = factory.buildGetter(Car[].class, "[3].model");

        Assert.assertEquals("Kalina", getter.getValue(cars));
    }
}
