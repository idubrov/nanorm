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
import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.test.beans.Car;
import com.google.code.nanorm.test.common.MapperTestBase;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
public class SimpleResultMapTest extends MapperTestBase {
    public interface Mapper1 {
        // No result map -- automatic mapping (no default map as well)
        @Select("SELECT id, model, year FROM cars WHERE ID = ${1}")
        Car getCarById1(int id);
        
        // Test automatic is off by default
        @ResultMap(mappings = {
            @Mapping(property = "owner.firstName", column = "owner") })
        @Select("SELECT id, owner FROM cars WHERE ID = ${1}")
        Car getCarById2(int id);
        
        // Turning on automatic mapping
        @ResultMap(auto = true, mappings = {
            @Mapping(property = "owner.firstName", column = "owner") })
        @Select("SELECT id, model, owner FROM cars WHERE ID = ${1}")
        Car getCarById3(int id);
    }
    
    @Test
    /**
     * Test automatic result mapping
     */
    public void testResultMap1() {
        Mapper1 mapper = factory.createMapper(Mapper1.class);
        Car car = mapper.getCarById1(2);
        Assert.assertEquals(2, car.getId());
        Assert.assertEquals("Ford Focus", car.getModel());
        Assert.assertEquals(2004, car.getYear());
        Assert.assertEquals(null, car.getOwner().getFirstName());
        Assert.assertEquals(null, car.getOwner().getLastName());
    }
    
    @Test
    /**
     * Test no automatic result mapping by default
     */
    public void testResultMap2() {
        Mapper1 mapper = factory.createMapper(Mapper1.class);
        Car car = mapper.getCarById2(2);
        Assert.assertEquals(0, car.getId());
        Assert.assertEquals(null, car.getModel());
        Assert.assertEquals(0, car.getYear());
        Assert.assertEquals("John", car.getOwner().getFirstName());
        Assert.assertEquals(null, car.getOwner().getLastName());
    }
    
    @Test
    /**
     * Test automatic result mapping
     */
    public void testResultMap3() {
        Mapper1 mapper = factory.createMapper(Mapper1.class);
        Car car = mapper.getCarById3(2);
        Assert.assertEquals(2, car.getId());
        Assert.assertEquals("Ford Focus", car.getModel());
        Assert.assertEquals(0, car.getYear());
        Assert.assertEquals("John", car.getOwner().getFirstName());
        Assert.assertEquals(null, car.getOwner().getLastName());
    }
}
