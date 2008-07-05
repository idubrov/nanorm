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
package com.google.code.nanorm.test.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;

import com.google.code.nanorm.NanormFactory;
import com.google.code.nanorm.Session;
import com.google.code.nanorm.config.NanormConfiguration;

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 29.05.2008
 */
public class MapperTestBase {
    
	/**
	 * Connection.
	 */
    protected Connection conn;
    
    /**
     * Nanorm factory.
     */
    protected NanormFactory factory;
    
    /**
     * Current transaction.
     */
    protected Session transaction;
    
    /**
     * Loads the test data.
     * @throws Exception any error
     */
    @Before
    public void setUp() throws Exception {
        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection("jdbc:h2:mem:", "sa", "");
        
        // Create some cars
        execute(
            "CREATE TABLE CARS(id INTEGER, owner VARCHAR(50), model VARCHAR(50), year INTEGER)");
        
        execute(
            "INSERT INTO CARS(id, owner, model, year) VALUES (1, 'Rocky', 'Toyota Vista', 2006)");
        
        execute(
            "INSERT INTO CARS(id, owner, model, year) VALUES (2, 'John', 'Ford Focus', 2004)");
        
        // Tables for primitive values
        execute(
            "CREATE TABLE PRIMITIVE(id INTEGER, primByte TINYINT, wrapByte TINYINT, " +
            "primShort SMALLINT, wrapShort SMALLINT, primInt INT, wrapInt INT," +
            "primLong BIGINT, wrapLong BIGINT, primBoolean BOOL, wrapBoolean BOOL," +
            "primChar CHAR(1), wrapChar CHAR(1), primFloat REAL, wrapFloat REAL," +
            "primDouble DOUBLE, wrapDouble DOUBLE, string VARCHAR(50))");
        
        execute(
            "INSERT INTO PRIMITIVE(id, primByte, wrapByte, primShort, wrapShort, " +
            "primInt, wrapInt, primLong, wrapLong, primBoolean, wrapBoolean, " +
            "primChar, wrapChar, primFloat, wrapFloat, primDouble, wrapDouble, string) VALUES(" +
            "1, 37, -23, 8723, -6532, " +
            "824756237, -123809163, 282347987987234987, -23429879871239879, TRUE, FALSE," +
            "'a', 'H', 34.5, -25.25, " +
            "44.5, -47.125, 'Hello, H2!')");
        
        // Create some owners
        execute(
            "CREATE TABLE OWNERS(id INTEGER, car_id INTEGER, firstName VARCHAR(50), lastName VARCHAR(50))");
        
        execute(
            "INSERT INTO OWNERS(id, car_id, firstName, lastName) VALUES (1, 1, 'Bobby', 'Brown')");
        
        execute(
            "INSERT INTO OWNERS(id, car_id, firstName, lastName) VALUES (2, 1, 'Jimmy', 'Green')");
        
        // Create some crashes
        execute("CREATE TABLE CRASHES(id INTEGER, owner_id INTEGER, year INTEGER)");
        execute("INSERT INTO CRASHES(id, owner_id, year) VALUES (101, 1, 2006)");
        execute("INSERT INTO CRASHES(id, owner_id, year) VALUES (102, 1, 2007)");
        
        conn.commit();
        
        factory = new NanormConfiguration().buildFactory();
        transaction = factory.openSession(conn);
        
    }
    
    /**
     * Execute the statement.
     * @param sql sql statement to execute
     * @throws SQLException any SQL error
     */
    protected void execute(String sql) throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.execute(sql);
        } finally {
            st.close();
        }
    }
    
    /**
     * Rollback the transaction and close the connection.
     * @throws SQLException any SQL exception 
     */
    @After
    public void tearDown() throws SQLException {
        transaction.rollback();
        transaction.end();
        conn.close();
    }

}
