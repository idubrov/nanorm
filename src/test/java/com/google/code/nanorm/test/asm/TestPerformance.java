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

import org.junit.Ignore;
import org.junit.Test;

import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;
import com.google.code.nanorm.internal.introspect.asm.ASMIntrospectionFactory;
import com.google.code.nanorm.internal.introspect.reflect.ReflectIntrospectionFactory;
import com.google.code.nanorm.test.beans.Car;
import com.google.code.nanorm.test.beans.Crash;
import com.google.code.nanorm.test.beans.Owner;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 19.06.2008
 */
public class TestPerformance {

    
    @Ignore
    @Test
    public void testGetterArray() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        IntrospectionFactory factory1 = new ASMIntrospectionFactory(cl);
        IntrospectionFactory factory2 = new ReflectIntrospectionFactory();
        
        
        Owner owner = new Owner();
        Crash crash = new Crash();
        crash.setYear(2007);
        Crash[] crashes = new Crash[10];
        crashes[3] = crash;
        owner.setCrashes2(crashes);
        Car car = new Car();
        car.setOwner(owner);
            
        Getter getter1 = factory1.buildGetter(Car.class, "owner.crashes2[3].year");
        Getter getter2 = factory2.buildGetter(Car.class, "owner.crashes2[3].year");
        
        for(int i = 0; i < 100000; ++i) {
            getter1.getValue(car);
            getter2.getValue(car);
        }
        
        long t1 = System.currentTimeMillis();
        for(int i = 0; i < 1000000; ++i) {
            getter1.getValue(car);
        }
        long t2 = System.currentTimeMillis();

        long t3 = System.currentTimeMillis();
        for(int i = 0; i < 1000000; ++i) {
            getter2.getValue(car);
        }
        long t4 = System.currentTimeMillis();
        
        System.err.println("R: " + (t2 - t1) + " vs " + (t4 - t3)); 
        System.err.println((t4 - t3) / (t2 - t1));

        //Assert.assertEquals(2007, getter1.getValue(car));
    }
}
