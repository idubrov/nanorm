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

import junit.framework.Assert;

import org.junit.Test;

import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.test.common.MapperTestBase;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
@SuppressWarnings("all")
public class PropertiesResultMapTest extends MapperTestBase {
    public static class TestBean {
        private int id;
        
        private String moDeL;

        /** @return Returns the id. */
        public int getId() {
            return id;
        }

        /** @param id The id to set. */
        public void setId(int id) {
            this.id = id;
        }

        /** @return Returns the moDeL. */
        public String getMoDeL() {
            return moDeL;
        }

        /** @param moDeL The moDeL to set. */
        public void setMoDeL(String moDeL) {
            this.moDeL = moDeL;
        }
    }
    
    public interface Mapper {
        @Select("SELECT id, model FROM cars WHERE ID = ${1}")
        TestBean getTestBean(int id);
    }
    
    @Test
    public void testAutoMappingCase() {
        Mapper mapper = factory.createMapper(Mapper.class);
        TestBean testBean = mapper.getTestBean(1);
        Assert.assertEquals(1, testBean.getId());
        Assert.assertEquals("Toyota Vista", testBean.getMoDeL());
    }
    
}