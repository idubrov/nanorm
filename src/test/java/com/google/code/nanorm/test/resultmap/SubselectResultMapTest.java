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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.code.nanorm.annotations.Mapping;
import com.google.code.nanorm.annotations.ResultMap;
import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.test.beans.Car;
import com.google.code.nanorm.test.beans.Car2;
import com.google.code.nanorm.test.beans.Owner;
import com.google.code.nanorm.test.common.MapperTestBase;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
@SuppressWarnings("all")
public class SubselectResultMapTest extends MapperTestBase {
    public interface Mapper {
        
        @ResultMap(mappings = {
            @Mapping(property = "firstName", column = "owner")
        })
        @Select("SELECT id, owner FROM cars WHERE id = ${1}")
        Owner getOwnerByCarId(int carId);
        
        @ResultMap(auto = true)
        @Select("SELECT id, firstName, lastName FROM owners WHERE car_id = ${1}")
        List<Owner> getOwnersByCarId(int carId);
        
        // Test 1-1 mapping with nested result map
        @ResultMap(mappings = {
            @Mapping(property = "id"),
            @Mapping(property = "model"),
            @Mapping(property = "year"),
            @Mapping(property = "owner", column = "id", subselect = "getOwnerByCarId") 
        })
        @Select("SELECT id, model, year FROM cars WHERE id = ${1}")
        Car getCarById(int id);
        
        // Test 1-N mapping with nested result map, the property type is List
        @ResultMap(groupBy = "id", mappings = {
            @Mapping(property = "id"),
            @Mapping(property = "model"),
            @Mapping(property = "year"),
            @Mapping(property = "owners", column = "id", subselect = "getOwnersByCarId") 
        })
        @Select("SELECT id, model, year FROM cars WHERE id = ${1}")
        Car2 getCarById3(int id);
    }

   @Test
    public void testNestedOneToOne() {
        Mapper mapper = factory.createMapper(Mapper.class);
        Car car = mapper.getCarById(1);
        Assert.assertEquals(1, car.getId());
        Assert.assertEquals("Rocky", car.getOwner().getFirstName());
        Assert.assertEquals("Toyota Vista", car.getModel());
        Assert.assertEquals(2006, car.getYear());
    }
   
    @Test
    public void testNestedOneToMany() {
        Mapper mapper = factory.createMapper(Mapper.class);
        Car2 car = mapper.getCarById3(1);
        Assert.assertEquals(1, car.getId());
        Assert.assertEquals(2006, car.getYear());
        Assert.assertEquals(2, car.getOwners().size());
        
        Assert.assertEquals(1, car.getOwners().get(0).getId());
        Assert.assertEquals("Bobby", car.getOwners().get(0).getFirstName());
        Assert.assertEquals("Brown", car.getOwners().get(0).getLastName());
        
        Assert.assertEquals(2, car.getOwners().get(1).getId());
        Assert.assertEquals("Jimmy", car.getOwners().get(1).getFirstName());
        Assert.assertEquals("Green", car.getOwners().get(1).getLastName());
        
        
    }
}
