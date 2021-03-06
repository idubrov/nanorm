/**
 * Copyright (C) 2008, 2009 Ivan S. Dubrov
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

import com.google.code.nanorm.annotations.Property;
import com.google.code.nanorm.annotations.ResultMap;
import com.google.code.nanorm.annotations.ResultMapList;
import com.google.code.nanorm.annotations.ResultMapRef;
import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.exceptions.ConfigurationException;
import com.google.code.nanorm.test.beans.Publication;
import com.google.code.nanorm.test.common.MapperTestBase;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
@SuppressWarnings("all")
public class TestResultMapRef extends MapperTestBase {
    @ResultMapList( {
            @ResultMap(id = "pub0", mappings = {
                    @Property(value = "article.subject", column = "subject"),
                    @Property(value = "article.body", column = "body") }),
            @ResultMap(id = "pub1", auto = true, mappings = {
                    @Property(value = "article.subject", column = "subject"),
                    @Property(value = "article.body", column = "body") }) })
    @ResultMap(id = "pub2", auto = true, mappings = {
            @Property(value = "article.subject", column = "subject"),
            @Property(value = "article.body", column = "body") })
    public interface Mapper1 {

        // Reference to the item in the list on the interface
        @ResultMapRef("pub1")
        @Select("SELECT id, subject, body, year FROM articles WHERE ID = ${1}")
        Publication getPublicationByIdRef1(int id);

        // Reference to the item on the interface
        @ResultMapRef("pub2")
        @Select("SELECT id, subject, body, year FROM articles WHERE ID = ${1}")
        Publication getPublicationByIdRef2(int id);
    }

    public interface Mapper2 {
        // Missing reference
        @ResultMapRef("pub3")
        @Select("SELECT id, subject, body, year FROM articles WHERE ID = ${1}")
        Publication getPublicationByIdRef3(int id);
    }

    public interface Mapper3 extends Mapper1 {
        // Reference to the item in the list on the superinterface
        @ResultMapRef("pub1")
        @Select("SELECT id, subject, body, year FROM articles WHERE ID = ${1}")
        Publication getPublicationByIdRef4(int id);

        // Refernce to the item on the superinterface
        @ResultMapRef("pub2")
        @Select("SELECT id, subject, body, year FROM articles WHERE ID = ${1}")
        Publication getPublicationByIdRef5(int id);
    }

    // Test default result map reference
    @ResultMap(mappings = {@Property(value = "article.subject", column = "subject") })
    public interface Mapper4 {

        // Using the default map
        @Select("SELECT id, subject FROM articles WHERE ID = ${1}")
        Publication getPublicationByIdRef6(int id);
    }

    @Test
    public void testResultMapRef1() throws Exception {
        Mapper1 mapper = factory.createMapper(Mapper1.class);
        Publication pub = mapper.getPublicationByIdRef1(1);
        Assert.assertEquals(1, pub.getId());
    }

    @Test
    public void testResultMapRef2() throws Exception {
        Mapper1 mapper = factory.createMapper(Mapper1.class);
        Publication pub = mapper.getPublicationByIdRef2(1);
        Assert.assertEquals(1, pub.getId());
    }

    @Test
    /**
     * Test accessing missing result mapping reference
     */
    public void testResultMapRef3() {
        try {
            Mapper2 mapper = factory.createMapper(Mapper2.class);
            mapper.getPublicationByIdRef3(1);
            Assert.fail();
        } catch (ConfigurationException e) {
            // That's ok, result map reference is missing
        }
    }

    @Test
    public void testResultMapRef4() throws Exception {
        Mapper3 mapper = factory.createMapper(Mapper3.class);
        Publication pub = mapper.getPublicationByIdRef4(1);
        Assert.assertEquals(1, pub.getId());
    }

    @Test
    public void testResultMapRef5() throws Exception {
        Mapper3 mapper = factory.createMapper(Mapper3.class);
        Publication pub = mapper.getPublicationByIdRef5(1);
        Assert.assertEquals(1, pub.getId());
    }

    @Test
    /**
     * Test no automatic result mapping by default
     */
    public void testResultMap6() throws Exception {
        Mapper4 mapper = factory.createMapper(Mapper4.class);
        Publication pub = mapper.getPublicationByIdRef6(2);
        Assert.assertEquals(0, pub.getId());
        Assert.assertEquals(0, pub.getYear());
        Assert.assertEquals("Saving the Earth", pub.getArticle().getSubject());
        Assert.assertEquals(null, pub.getArticle().getBody());
    }

}
