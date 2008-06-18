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

import org.junit.Assert;
import org.junit.Test;

import com.google.code.nanorm.annotations.Mapping;
import com.google.code.nanorm.annotations.ResultMap;
import com.google.code.nanorm.annotations.ResultMapList;
import com.google.code.nanorm.annotations.ResultMapRef;
import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.test.beans.Car;
import com.google.code.nanorm.test.beans.Car2;
import com.google.code.nanorm.test.common.MapperTestBase;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
public class NestedResultMapTest extends MapperTestBase {
    @ResultMapList({
        @ResultMap(id = "owner", mappings = {
            @Mapping(property = "firstName", column = "owner")
        }),
        @ResultMap(id = "owners", mappings = {
            @Mapping(property = "firstName", column = "owner")
        }),
        @ResultMap(id = "owners2", mappings = {
            @Mapping(property = "id", column = "owner_id"),
            @Mapping(property = "firstName"),
            @Mapping(property = "lastName")
        }),
        @ResultMap(id = "crashes", mappings = {
            @Mapping(property = "id", column = "crash_id"),
            @Mapping(property = "year", column = "crash_year")
        }),
        // groupBy is the name of the property
        @ResultMap(groupBy = "id", id = "owners3", mappings = {
            @Mapping(property = "id", column = "owner_id"),
            @Mapping(property = "firstName"),
            @Mapping(property = "lastName"),
            @Mapping(property = "crashes", resultMap = @ResultMapRef("crashes"))
        })
    })
    public interface Mapper {
        
        // Test 1-1 mapping with nested result map
        @ResultMap(mappings = {
            @Mapping(property = "id"),
            @Mapping(property = "model"),
            @Mapping(property = "year"),
            @Mapping(property = "owner", resultMap = @ResultMapRef("owner")) 
        })
        @Select("SELECT id, owner, model, year FROM cars WHERE ID = ${1}")
        Car getCarById(int id);
        
        // Test 1-1 mapping with nested result map, the property type is List
        @ResultMap(mappings = {
            @Mapping(property = "id"),
            @Mapping(property = "model"),
            @Mapping(property = "year"),
            @Mapping(property = "owners", resultMap = @ResultMapRef("owners")) 
        })
        @Select("SELECT id, owner, model, year FROM cars WHERE ID = ${1}")
        Car2 getCarById2(int id);
        
        // Test 1-N mapping with nested result map, the property type is List
        @ResultMap(groupBy = "id", mappings = {
            @Mapping(property = "id"),
            @Mapping(property = "model"),
            @Mapping(property = "year"),
            @Mapping(property = "owners", resultMap = @ResultMapRef("owners2")) 
        })
        @Select("SELECT cars.id, cars.model, cars.year, " +
        		"owners.id as owner_id, owners.firstName, owners.lastName FROM cars " +
                "INNER JOIN owners ON cars.id = owners.car_id WHERE cars.id = ${1}" +
                "ORDER BY cars.id, owners.id")
        Car2 getCarById3(int id);
        
        // Test 1-N-M mapping with two nested result map, the property type is List
        @ResultMap(groupBy = "id", mappings = {
            @Mapping(property = "id"),
            @Mapping(property = "model"),
            @Mapping(property = "year"),
            @Mapping(property = "owners", resultMap = @ResultMapRef("owners3")) 
        })
        @Select("SELECT cars.id, cars.model, cars.year, " +
                "owners.id as owner_id, owners.firstName, owners.lastName, " +
                "crashes.id as crash_id, crashes.year as crash_year " +
                "FROM cars " +
                "INNER JOIN owners ON cars.id = owners.car_id " +
                "INNER JOIN crashes ON owners.id = crashes.owner_id " +
                "WHERE cars.id = ${1} " +
                "ORDER BY cars.id, owners.id, crashes.id")
        Car2 getCarById4(int id);
        
        // TODO: More nested mappings
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
    public void testNestedOneToOne2() {
        Mapper mapper = factory.createMapper(Mapper.class);
        Car2 car = mapper.getCarById2(1);
        Assert.assertEquals(1, car.getId());
        Assert.assertEquals(1, car.getOwners().size());
        Assert.assertEquals("Rocky", car.getOwners().get(0).getFirstName());
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
    
    @Test
    public void testNestedOneToMany2() {
        Mapper mapper = factory.createMapper(Mapper.class);
        Car2 car = mapper.getCarById4(1);
        Assert.assertEquals(1, car.getId());
        Assert.assertEquals(2006, car.getYear());
        
        Assert.assertEquals(1, car.getOwners().size());
        
        Assert.assertEquals(1, car.getOwners().get(0).getId());
        Assert.assertEquals("Bobby", car.getOwners().get(0).getFirstName());
        Assert.assertEquals("Brown", car.getOwners().get(0).getLastName());
        
        Assert.assertEquals(2, car.getOwners().get(0).getCrashes().size());
        
        Assert.assertEquals(101, car.getOwners().get(0).getCrashes().get(0).getId());
        Assert.assertEquals(2006, car.getOwners().get(0).getCrashes().get(0).getYear());
        
        Assert.assertEquals(102, car.getOwners().get(0).getCrashes().get(1).getId());
        Assert.assertEquals(2007, car.getOwners().get(0).getCrashes().get(1).getYear());
        
        // Second owner
        /*
        Assert.assertEquals(2, car.getOwners().get(1).getId());
        Assert.assertEquals("Jimmy", car.getOwners().get(1).getFirstName());
        Assert.assertEquals("Green", car.getOwners().get(1).getLastName());
        Assert.assertEquals(0, car.getOwners().get(1).getCrashes().size());
        */
    }
}
