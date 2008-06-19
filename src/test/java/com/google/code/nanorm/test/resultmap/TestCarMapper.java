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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.code.nanorm.Factory;
import com.google.code.nanorm.Transaction;
import com.google.code.nanorm.internal.FactoryImpl;
import com.google.code.nanorm.test.beans.Car;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
public class TestCarMapper {
    
    private Connection conn;
    
    private Factory factory;
    
    private Transaction transaction;
    
    @Before
    public void setUp() throws Exception {
        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection("jdbc:h2:mem:", "sa", "");
        
        execute(
            "CREATE TABLE CARS(id INTEGER, owner VARCHAR(50), model VARCHAR(50), year INTEGER)");
        
        execute(
            "INSERT INTO CARS(id, owner, model, year) VALUES (10, 'Rocky', 'Toyota Vista', 2006)");
        
        execute(
            "INSERT INTO CARS(id, owner, model, year) VALUES (11, 'John', 'Ford Focus', 2004)");
        conn.commit();
        
        factory = new FactoryImpl();
        transaction = factory.useConnection(conn);
        
    }
    
    protected void execute(String sql) throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.execute(sql);
        } finally {
            st.close();
        }
    }
    
    @After
    public void tearDown() throws Exception {
        transaction.rollback();
        transaction.end();
        conn.close();
    }

    @Test
    public void testSelectById() {
        CarMapper mapper = factory.createMapper(CarMapper.class);
        
        Car car = mapper.getCarById(10);
        Assert.assertEquals(10, car.getId());
        Assert.assertEquals("Toyota Vista", car.getModel());
        Assert.assertEquals("Rocky", car.getOwner().getFirstName());
        Assert.assertEquals(2006, car.getYear());
    }
    
    @Test
    public void testSelectList() {
        CarMapper mapper = factory.createMapper(CarMapper.class);

        List<Car> cars = mapper.listCars();
        Assert.assertEquals(2, cars.size());
        
        Car car = cars.get(0);
        Assert.assertEquals(10, car.getId());
        Assert.assertEquals("Toyota Vista", car.getModel());
        Assert.assertEquals("Rocky", car.getOwner().getFirstName());
        Assert.assertEquals(2006, car.getYear());
        
        car = cars.get(1);
        Assert.assertEquals(11, car.getId());
        Assert.assertEquals("Ford Focus", car.getModel());
        Assert.assertEquals("John", car.getOwner().getFirstName());
        Assert.assertEquals(2004, car.getYear());
    }
    
    @Test
    public void testListByModelYear() {
        CarMapper mapper = factory.createMapper(CarMapper.class);
        
        List<Car> cars = mapper.listByModelYear("Ford Focus", 0);
        Assert.assertEquals(1, cars.size());
        
        Car car = cars.get(0);
        Assert.assertEquals(11, car.getId());
    }
    
    @Test
    public void testListByModelYear2() {
        CarMapper mapper = factory.createMapper(CarMapper.class);
        
        List<Car> cars = mapper.listByModelYear(null, 2006);
        Assert.assertEquals(1, cars.size());
        
        Car car = cars.get(0);
        Assert.assertEquals(10, car.getId());
    }
    
    @Test
    public void testListByModelYear3() {
        CarMapper mapper = factory.createMapper(CarMapper.class);
        
        List<Car> cars = mapper.listByModelYear("Ford Focus", 2006);
        Assert.assertEquals(0, cars.size());
    }

}
