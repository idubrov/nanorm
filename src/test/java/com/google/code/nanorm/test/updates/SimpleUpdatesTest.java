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
import com.google.code.nanorm.test.beans.Article;
import com.google.code.nanorm.test.common.MapperTestBase;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
@SuppressWarnings("all")
public class SimpleUpdatesTest extends MapperTestBase {
    public interface Mapper1 {
        // No result map -- automatic mapping
        @Select("SELECT id, subject, year FROM articles WHERE ID = ${1}")
        Article getArticleById(int id);

        @Insert("INSERT INTO articles(id, subject, year) VALUES (${1.id}, ${1.subject}, ${1.year})")
        void insertArticle(Article article);
        
        @Update("INSERT INTO articles(id, subject, year) VALUES (${1.id}, ${1.subject}, ${1.year})")
        int insertArticle2(Article article);
        
        // Select key and return
        @Insert("INSERT INTO articles(id, subject, year) VALUES (next value for ids, ${1.subject}, ${1.year})")
        @SelectKey(value = "SELECT CURRVAL('ids')", type = SelectKeyType.AFTER)
        int insertArticle3(Article article);
        
        // Select key, set it to the parameter property and return
        @Insert("INSERT INTO articles(id, subject, year) VALUES (next value for ids, ${1.subject}, ${1.year})")
        @SelectKey(value = "SELECT CURRVAL('ids')", type = SelectKeyType.AFTER, property = "1.id")
        int insertArticle4(Article article);
        
        // Select key using JDBC
        @Insert("INSERT INTO articles(id, subject, year) VALUES (next value for ids, ${1.subject}, ${1.year})")
        @SelectKey(type = SelectKeyType.AFTER)
        int insertArticle5(Article article);
        
        // Pre-generate the key
        @Insert("INSERT INTO articles(id, subject, year) VALUES (${1.id}, ${1.subject}, ${1.year})")
        @SelectKey(value = "SELECT NEXTVAL('ids')", type = SelectKeyType.BEFORE, property = "1.id")
        int insertArticle6(Article article);
    }
    
    @Test
    /**
     * Test automatic result mapping
     */
    public void testInsert() {
        Mapper1 mapper = factory.createMapper(Mapper1.class);

        Article article1 = new Article();
        article1.setId(1001);
        article1.setSubject("Kalina");
        article1.setYear(2008);
        
        mapper.insertArticle(article1);
        
        Article article2 = mapper.getArticleById(1001);
        Assert.assertEquals(article1.getId(), article2.getId());
        Assert.assertEquals(article1.getSubject(), article2.getSubject());
        Assert.assertEquals(article1.getYear(), article2.getYear());
    }
    
    @Test
    /**
     * Test automatic result mapping
     */
    public void testInsert2() {
        Mapper1 mapper = factory.createMapper(Mapper1.class);

        Article article1 = new Article();
        article1.setId(1002);
        article1.setSubject("Kalina");
        article1.setYear(2008);
        
        Assert.assertEquals(1, mapper.insertArticle2(article1));
        
        Article article2 = mapper.getArticleById(1002);
        Assert.assertEquals(article1.getId(), article2.getId());
        Assert.assertEquals(article1.getSubject(), article2.getSubject());
        Assert.assertEquals(article1.getYear(), article2.getYear());
    }
    
    @Test
    /**
     * Test automatic result mapping
     */
    public void testInsert3() throws SQLException {
        Mapper1 mapper = factory.createMapper(Mapper1.class);

        Article article1 = new Article();
        article1.setId(456);
        article1.setSubject("Modello");
        article1.setYear(2008);

        execute("ALTER SEQUENCE ids RESTART WITH 1432"); 
        int id = mapper.insertArticle3(article1);
        // Article has old key, because we didn't specify the "property" on SelectKey. 
        Assert.assertEquals(456, article1.getId()); 
        article1.setId(id);
        Assert.assertEquals(1432, id);
        
        Article article2 = mapper.getArticleById(id);
        Assert.assertEquals(article1.getId(), article2.getId());
        Assert.assertEquals(article1.getSubject(), article2.getSubject());
        Assert.assertEquals(article1.getYear(), article2.getYear());
    }
    
    @Test
    /**
     * Test automatic result mapping
     */
    public void testInsert4() throws SQLException {
        Mapper1 mapper = factory.createMapper(Mapper1.class);

        Article article1 = new Article();
        article1.setId(456);
        article1.setSubject("Modello");
        article1.setYear(2008);

        execute("ALTER SEQUENCE ids RESTART WITH 1437"); 
        int id = mapper.insertArticle4(article1);
        // Now framework sets it, because we specified "property" on SelectKey
        Assert.assertEquals(1437, article1.getId());
        Assert.assertEquals(1437, id);
        
        Article article2 = mapper.getArticleById(id);
        Assert.assertEquals(article1.getId(), article2.getId());
        Assert.assertEquals(article1.getSubject(), article2.getSubject());
        Assert.assertEquals(article1.getYear(), article2.getYear());
    }
    
    @Test
    /**
     * Test automatic result mapping
     */
    public void testInsert5() throws SQLException {
        Mapper1 mapper = factory.createMapper(Mapper1.class);

        Article article1 = new Article();
        article1.setId(456);
        article1.setSubject("Modello");
        article1.setYear(2008);

        execute("ALTER SEQUENCE ids RESTART WITH 1732"); 
        int id = mapper.insertArticle5(article1);
        // Article has old key, because we didn't specify the "property" on SelectKey. 
        Assert.assertEquals(456, article1.getId()); 
        article1.setId(id);
        Assert.assertEquals(1732, id);
        
        Article article2 = mapper.getArticleById(id);
        Assert.assertEquals(article1.getId(), article2.getId());
        Assert.assertEquals(article1.getSubject(), article2.getSubject());
        Assert.assertEquals(article1.getYear(), article2.getYear());
    }
    
    @Test
    /**
     * Test automatic result mapping
     */
    public void testInsert6() throws SQLException {
        Mapper1 mapper = factory.createMapper(Mapper1.class);

        Article article1 = new Article();
        article1.setId(456);
        article1.setSubject("Modello");
        article1.setYear(2008);

        execute("ALTER SEQUENCE ids RESTART WITH 1565"); 
        int id = mapper.insertArticle6(article1);
        Assert.assertEquals(1565, id);
        Assert.assertEquals(1565, article1.getId());

        Article article2 = mapper.getArticleById(id);
        Assert.assertEquals(article1.getId(), article2.getId());
        Assert.assertEquals(article1.getSubject(), article2.getSubject());
        Assert.assertEquals(article1.getYear(), article2.getYear());
    }
}
