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

import org.junit.Assert;
import org.junit.Test;

import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.test.common.MapperTestBase;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
@SuppressWarnings("all")
public class TestPropertiesResultMap extends MapperTestBase {
    public static class TestBean {
        private int id;
        
        private String subJeCT;

        /** @return Returns the id. */
        public int getId() {
            return id;
        }

        /** @param id The id to set. */
        public void setId(int id) {
            this.id = id;
        }

        /** @return Returns the subJeCT. */
        public String getSubJeCT() {
            return subJeCT;
        }

        /** @param subJeCT The subJeCT to set. */
        public void setSubJeCT(String moDeL) {
            this.subJeCT = moDeL;
        }
    }
    
    public interface Mapper {
        @Select("SELECT id, subject FROM articles WHERE ID = ${1}")
        TestBean getTestBean(int id);
    }
    
    @Test
    public void testAutoMappingCase() throws Exception {
        Mapper mapper = factory.createMapper(Mapper.class);
        TestBean testBean = mapper.getTestBean(1);
        Assert.assertEquals(1, testBean.getId());
        Assert.assertEquals("World Domination", testBean.getSubJeCT());
    }
    
}