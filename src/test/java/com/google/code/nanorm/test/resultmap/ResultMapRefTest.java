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
package com.google.code.nanorm.test.resultmap;

import junit.framework.Assert;

import org.junit.Test;

import com.google.code.nanorm.annotations.Mapping;
import com.google.code.nanorm.annotations.ResultMap;
import com.google.code.nanorm.annotations.ResultMapList;
import com.google.code.nanorm.annotations.ResultMapRef;
import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.exceptions.ConfigurationException;
import com.google.code.nanorm.test.beans.Car;
import com.google.code.nanorm.test.common.MapperTestBase;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
@SuppressWarnings("all")
public class ResultMapRefTest extends MapperTestBase {
    @ResultMapList( {
        @ResultMap(id = "car0", mappings = {
            @Mapping(property = "owner.firstName", column = "owner") }),
        @ResultMap(id = "car1", auto = true, mappings = {
            @Mapping(property = "owner.firstName", column = "owner") }) })
    @ResultMap(id = "car2", auto = true, mappings = {
            @Mapping(property = "owner.firstName", column = "owner") })
    public interface Mapper1 {

        // Reference to the item in the list on the interface
        @ResultMapRef("car1")
        @Select("SELECT id, model, owner, year FROM cars WHERE ID = ${1}")
        Car getCarByIdRef1(int id);

        // Refernce to the item on the interface
        @ResultMapRef("car2")
        @Select("SELECT id, model, owner, year FROM cars WHERE ID = ${1}")
        Car getCarByIdRef2(int id);
    }

    public interface Mapper2 {
        // Missing reference
        @ResultMapRef("car3")
        @Select("SELECT id, model, owner, year FROM cars WHERE ID = ${1}")
        Car getCarByIdRef3(int id);
    }

    public interface Mapper3 extends Mapper1 {
        // Reference to the item in the list on the superinterface
        @ResultMapRef("car1")
        @Select("SELECT id, model, owner, year FROM cars WHERE ID = ${1}")
        Car getCarByIdRef4(int id);

        // Refernce to the item on the superinterface
        @ResultMapRef("car2")
        @Select("SELECT id, model, owner, year FROM cars WHERE ID = ${1}")
        Car getCarByIdRef5(int id);
    }

    // Test default result map reference
    @ResultMap(mappings = {
            @Mapping(property = "owner.firstName", column = "owner") })
    public interface Mapper4 {

        // Using the default map
        @Select("SELECT id, owner FROM cars WHERE ID = ${1}")
        Car getCarByIdRef6(int id);
    }
    
    @Test
    public void testResultMapRef1() {
        Mapper1 mapper = factory.createMapper(Mapper1.class);
        Car car = mapper.getCarByIdRef1(1);
        Assert.assertEquals(1, car.getId());
    }

    @Test
    public void testResultMapRef2() {
        Mapper1 mapper = factory.createMapper(Mapper1.class);
        Car car = mapper.getCarByIdRef2(1);
        Assert.assertEquals(1, car.getId());
    }

    @Test
    /**
     * Test accessing missing result mapping reference
     */
    public void testResultMapRef3() {
        try {
            Mapper2 mapper = factory.createMapper(Mapper2.class);
            mapper.getCarByIdRef3(1);
            Assert.fail();
        } catch (ConfigurationException e) {
            // That's ok, result map reference is missing
        }
    }
    
    @Test
    public void testResultMapRef4() {
        Mapper3 mapper = factory.createMapper(Mapper3.class);
        Car car = mapper.getCarByIdRef4(1);
        Assert.assertEquals(1, car.getId());
    }

    @Test
    public void testResultMapRef5() {
        Mapper3 mapper = factory.createMapper(Mapper3.class);
        Car car = mapper.getCarByIdRef5(1);
        Assert.assertEquals(1, car.getId());
    }
    
    @Test
    /**
     * Test no automatic result mapping by default
     */
    public void testResultMap6() {
        Mapper4 mapper = factory.createMapper(Mapper4.class);
        Car car = mapper.getCarByIdRef6(2);
        Assert.assertEquals(0, car.getId());
        Assert.assertEquals(null, car.getModel());
        Assert.assertEquals(0, car.getYear());
        Assert.assertEquals("John", car.getOwner().getFirstName());
        Assert.assertEquals(null, car.getOwner().getLastName());
    }

}
