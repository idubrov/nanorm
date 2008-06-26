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
package com.google.code.nanorm.test.updates;

import junit.framework.Assert;

import org.junit.Test;

import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.annotations.Update;
import com.google.code.nanorm.test.beans.Car;
import com.google.code.nanorm.test.common.MapperTestBase;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
public class SimpleUpdatesTest extends MapperTestBase {
    public interface Mapper1 {
        // No result map -- automatic mapping (no default map as well)
        @Select("SELECT id, model, year FROM cars WHERE ID = ${1}")
        Car getCarById(int id);

        @Update("INSERT INTO cars(id, model, year) VALUES (${1.id}, ${1.model}, ${1.year})")
        void insertCar(Car car);
        
        @Update("INSERT INTO cars(id, model, year) VALUES (${1.id}, ${1.model}, ${1.year})")
        int insertCar2(Car car);
    }
    
    @Test
    /**
     * Test automatic result mapping
     */
    public void testInsert() {
        Mapper1 mapper = factory.createMapper(Mapper1.class);

        Car car1 = new Car();
        car1.setId(1001);
        car1.setModel("Kalina");
        car1.setYear(2008);
        
        mapper.insertCar(car1);
        
        Car car2 = mapper.getCarById(1001);
        Assert.assertEquals(car1.getId(), car2.getId());
        Assert.assertEquals(car1.getModel(), car2.getModel());
        Assert.assertEquals(car1.getYear(), car2.getYear());
    }
    
    @Test
    /**
     * Test automatic result mapping
     */
    public void testInsert2() {
        Mapper1 mapper = factory.createMapper(Mapper1.class);

        Car car1 = new Car();
        car1.setId(1002);
        car1.setModel("Kalina");
        car1.setYear(2008);
        
        Assert.assertEquals(1, mapper.insertCar2(car1));
        
        Car car2 = mapper.getCarById(1002);
        Assert.assertEquals(car1.getId(), car2.getId());
        Assert.assertEquals(car1.getModel(), car2.getModel());
        Assert.assertEquals(car1.getYear(), car2.getYear());
    }
}
