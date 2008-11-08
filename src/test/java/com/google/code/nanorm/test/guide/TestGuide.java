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

package com.google.code.nanorm.test.guide;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.google.code.nanorm.DataSink;
import com.google.code.nanorm.NanormFactory;
import com.google.code.nanorm.Session;
import com.google.code.nanorm.config.NanormConfiguration;

@SuppressWarnings("all")
public class TestGuide {
	
	@Test
	public void testGuide() throws Exception {
		NanormFactory factory = new NanormConfiguration().buildFactory();
		BookMapper mapper = factory.createMapper(BookMapper.class);
		
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection("jdbc:h2:mem:", "sa", "");
		
		// Create datatabase table
		Statement st = conn.createStatement();
		try {
			st.execute("CREATE TABLE BOOKS(id INTEGER PRIMARY KEY, name VARCHAR(100), author VARCHAR(100), published DATE)");
			st.execute("CREATE SEQUENCE SEQ START WITH 100 INCREMENT BY 1");
		} finally {
			try {
				st.close();
			} catch(SQLException e) {
				// Nothing.
			}
		}
		
		Session session = factory.openSession(conn);
		try {
			
			Book book = new Book();
			book.setId(1);
			book.setAuthor("Brain");
			book.setName("World Domination.");
			book.setPublished(new java.sql.Date(788922000)); // January 1, 1995

			mapper.insertBook(book); // Insert into the database

			book.setName("World Domination. Second Edition.");
			mapper.updateBook(book); // Update the book

			Book book2 = mapper.selectBook(1); // Select the book into other bean by id
			mapper.deleteBook(book2.getId()); // Delete the book record from the database
			
			// Generated keys
			book = new Book();
			book.setAuthor("Brain");
			book.setName("World Domination.");
			book.setPublished(new java.sql.Date(788922000)); // January 1, 1995
			
			int key = mapper.insertBook2(book);
			Assert.assertEquals(100, key);
			Assert.assertNotNull(mapper.selectBook(100));
			
			// Generated keys2
			book = new Book();
			book.setAuthor("Brain");
			book.setName("World Domination.");
			book.setPublished(new java.sql.Date(788922000)); // January 1, 1995
			
			key = mapper.insertBook3(book);
			Assert.assertEquals(101, key);
			Assert.assertNotNull(mapper.selectBook(101));			
			
			// Generated keys3
			book = new Book();
			book.setAuthor("Brain");
			book.setName("World Domination.");
			book.setPublished(new java.sql.Date(788922000)); // January 1, 1995
			
			mapper.insertBook4(book);
			Assert.assertEquals(102, book.getId());
			Assert.assertNotNull(mapper.selectBook(102));
			
			// Generated keys3
			book = new Book();
			book.setAuthor("Brain");
			book.setName("World Domination.");
			book.setPublished(new java.sql.Date(788922000)); // January 1, 1995
			
			mapper.insertBook5(book);
			Assert.assertEquals(103, book.getId());
			Assert.assertNotNull(mapper.selectBook(103));
			
			// List books using array
			Book[] books = mapper.listBooks();
			
			Assert.assertEquals(4, books.length);
			
			// using collection
			List<Book> books2 = mapper.listBooks2();
			Assert.assertEquals(4, books2.size());
			
			// Using data sink
			final Map<String, Book> map = new HashMap<String, Book>();
			mapper.listBooks3(new DataSink<Book>() {
				public void commitData() {
					// Nothing...
				}

				public void pushData(Book book) {
					map.put(book.getName(), book);
				}
			});
			Assert.assertEquals(1, map.size());
		} finally {
			session.end();
		}
		
	}
}
