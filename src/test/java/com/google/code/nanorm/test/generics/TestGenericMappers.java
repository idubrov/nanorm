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

package com.google.code.nanorm.test.generics;

import org.junit.Assert;
import org.junit.Test;

import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.test.beans.Publication;
import com.google.code.nanorm.test.common.MapperTestBase;

@SuppressWarnings("all")
public class TestGenericMappers extends MapperTestBase {

    public interface Mapper1<S, T> {
        T getPublicationById1(S id);
    }

    public interface Mapper2 extends Mapper1<Integer, Publication> {
        @Select("SELECT id, subject as title, year FROM articles WHERE ID = ${1}")
        Publication getPublicationById1(Integer id);
    }

    @Test
    /**
     * Test automatic result mapping
     */
    public void testResultMap1() throws Exception {
        Mapper2 mapper = factory.createMapper(Mapper2.class);
        Publication pub = mapper.getPublicationById1(2);
        Assert.assertEquals(2, pub.getId());
        Assert.assertEquals(2008, pub.getYear());
        Assert.assertEquals(null, pub.getArticle().getSubject());
        Assert.assertEquals(null, pub.getArticle().getBody());
    }

}
