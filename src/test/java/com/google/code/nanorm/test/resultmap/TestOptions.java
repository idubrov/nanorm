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

import static org.junit.Assert.assertEquals;

import java.sql.ResultSet;

import org.junit.Test;

import com.google.code.nanorm.Session;
import com.google.code.nanorm.annotations.Options;
import com.google.code.nanorm.annotations.Scalar;
import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.test.common.MapperTestBase;

/**
 * Test the fetch size option.
 * 
 * @author Ivan Dubrov
 */
@SuppressWarnings("all")
public class TestOptions extends MapperTestBase {

    @Options(fetchSize = 10)
    public interface Mapper1 {
        @Select("SELECT id FROM core WHERE id = ${1}")
        @Options(fetchSize = 1)
        @Scalar
        int selectFetchOne(int id);

        @Select("SELECT id FROM core WHERE id = ${1}")
        @Scalar
        int selectFetchParent(int id);
    }

    /**
     * Test method for fetchSize option. Actually, this method does not test
     * anything.
     * @throws NoSuchMethodException
     */
    @Test
    public void testOptions() throws NoSuchMethodException {
        Mapper1 mapper = factory.createMapper(Mapper1.class);
        assertEquals(1, mapper.selectFetchOne(1));
        
        assertEquals(1, mapper.selectFetchParent(1));
    }
}
