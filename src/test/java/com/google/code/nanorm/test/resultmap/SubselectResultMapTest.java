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
import com.google.code.nanorm.test.beans.Publication;
import com.google.code.nanorm.test.beans.Category;
import com.google.code.nanorm.test.beans.Article;
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
            @Mapping(property = "subject", column = "subject")
        })
        @Select("SELECT id, subject FROM articles WHERE id = ${1}")
        Article getArticle(int id);
        
        @ResultMap(auto = true)
        @Select("SELECT id, subject, body FROM articles WHERE category_id = ${1}")
        List<Article> getArticlesByCategoryId(int id);
        
        // Test 1-1 mapping with nested result map
        @ResultMap(mappings = {
            @Mapping(property = "id"),
            @Mapping(property = "title"),
            @Mapping(property = "year"),
            @Mapping(property = "article", column = "article_id", subselect = "getArticle") 
        })
        @Select("SELECT id, title, year, article_id FROM publications WHERE id = ${1}")
        Publication getPublicationById(int id);
        
        // Test 1-N mapping with nested result map, the property type is List
        @ResultMap(groupBy = "id", mappings = {
            @Mapping(property = "id"),
            @Mapping(property = "title"),
            @Mapping(property = "year"),
            @Mapping(property = "articles", column = "id", subselect = "getArticlesByCategoryId") 
        })
        @Select("SELECT id, title, year FROM categories WHERE id = ${1}")
        Category getCategoryById3(int id);
    }

   @Test
    public void testSubselectOneToOne() {
        Mapper mapper = factory.createMapper(Mapper.class);
        Publication pub = mapper.getPublicationById(543);
        Assert.assertEquals(543, pub.getId());
        Assert.assertEquals("Best Way to World Dominate!", pub.getTitle());
        Assert.assertEquals("World Domination", pub.getArticle().getSubject());
        Assert.assertEquals(2008, pub.getYear());
    }
   
    @Test
    public void testNestedOneToMany() {
        Mapper mapper = factory.createMapper(Mapper.class);
        Category car = mapper.getCategoryById3(1);
        Assert.assertEquals(1, car.getId());
        Assert.assertEquals(2006, car.getYear());
        Assert.assertEquals(2, car.getArticles().size());
        
        Assert.assertEquals(1, car.getArticles().get(0).getId());
        Assert.assertEquals("World Domination", car.getArticles().get(0).getSubject());
        Assert.assertEquals("Everybody thinks of world domination.", car.getArticles().get(0).getBody());
        
        Assert.assertEquals(2, car.getArticles().get(1).getId());
        Assert.assertEquals("Saving the Earth", car.getArticles().get(1).getSubject());
        Assert.assertEquals("To save the earth you need...", car.getArticles().get(1).getBody());
    }
}
