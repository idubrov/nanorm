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

import java.sql.SQLException;

import junit.framework.Assert;

import org.junit.Test;

import com.google.code.nanorm.annotations.Insert;
import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.annotations.SelectKey;
import com.google.code.nanorm.annotations.SelectKeyType;
import com.google.code.nanorm.annotations.Update;
import com.google.code.nanorm.test.beans.Car;
import com.google.code.nanorm.test.common.MapperTestBase;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
@SuppressWarnings("all")
public class SimpleUpdatesTest extends MapperTestBase {
    public interface Mapper1 {
        // No result map -- automatic mapping (no default map as well)
        @Select("SELECT id, model, year FROM cars WHERE ID = ${1}")
        Car getCarById(int id);

        @Insert("INSERT INTO cars(id, model, year) VALUES (${1.id}, ${1.model}, ${1.year})")
        void insertCar(Car car);
        
        @Update("INSERT INTO cars(id, model, year) VALUES (${1.id}, ${1.model}, ${1.year})")
        int insertCar2(Car car);
        
        @Insert("INSERT INTO cars(id, model, year) VALUES (next value for ids, ${1.model}, ${1.year})")
        @SelectKey(value = "SELECT CURRVAL('ids')", type = SelectKeyType.AFTER)
        int insertCar3(Car car);
        
        @Insert("INSERT INTO cars(id, model, year) VALUES (next value for ids, ${1.model}, ${1.year})")
        @SelectKey(value = "SELECT CURRVAL('ids')", type = SelectKeyType.AFTER, property = "1.id")
        int insertCar4(Car car);
        
        @Insert("INSERT INTO cars(id, model, year) VALUES (${1.id}, ${1.model}, ${1.year})")
        @SelectKey(value = "SELECT NEXTVAL('ids')", type = SelectKeyType.BEFORE, property = "1.id")
        int insertCar5(Car car);
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
    
    @Test
    /**
     * Test automatic result mapping
     */
    public void testInsert3() throws SQLException {
        Mapper1 mapper = factory.createMapper(Mapper1.class);

        Car car1 = new Car();
        car1.setId(456);
        car1.setModel("Modello");
        car1.setYear(2008);

        execute("ALTER SEQUENCE ids RESTART WITH 1432"); 
        int id = mapper.insertCar3(car1);
        // TODO: Probably, the framework should set it.
        car1.setId(id);
        Assert.assertEquals(1432, id);
        
        Car car2 = mapper.getCarById(id);
        Assert.assertEquals(car1.getId(), car2.getId());
        Assert.assertEquals(car1.getModel(), car2.getModel());
        Assert.assertEquals(car1.getYear(), car2.getYear());
    }
    
    @Test
    /**
     * Test automatic result mapping
     */
    public void testInsert4() throws SQLException {
        Mapper1 mapper = factory.createMapper(Mapper1.class);

        Car car1 = new Car();
        car1.setId(456);
        car1.setModel("Modello");
        car1.setYear(2008);

        execute("ALTER SEQUENCE ids RESTART WITH 1437"); 
        int id = mapper.insertCar4(car1);
        // Now framework sets it
        //car1.setId(id);
        Assert.assertEquals(1437, id);
        
        Car car2 = mapper.getCarById(id);
        Assert.assertEquals(car1.getId(), car2.getId());
        Assert.assertEquals(car1.getModel(), car2.getModel());
        Assert.assertEquals(car1.getYear(), car2.getYear());
    }
    
    @Test
    /**
     * Test automatic result mapping
     */
    public void testInsert5() throws SQLException {
        Mapper1 mapper = factory.createMapper(Mapper1.class);

        Car car1 = new Car();
        car1.setId(456);
        car1.setModel("Modello");
        car1.setYear(2008);

        execute("ALTER SEQUENCE ids RESTART WITH 1565"); 
        int id = mapper.insertCar5(car1);
        Assert.assertEquals(1565, id);
        Assert.assertEquals(1565, car1.getId());

        Car car2 = mapper.getCarById(id);
        Assert.assertEquals(car1.getId(), car2.getId());
        Assert.assertEquals(car1.getModel(), car2.getModel());
        Assert.assertEquals(car1.getYear(), car2.getYear());
    }
}
