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
package com.google.code.nanorm.test.call;

import junit.framework.Assert;

import org.junit.Test;

import com.google.code.nanorm.annotations.Call;
import com.google.code.nanorm.annotations.Property;
import com.google.code.nanorm.annotations.ResultMap;
import com.google.code.nanorm.test.common.MapperTestBase;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
@SuppressWarnings("all")
public class CallTest extends MapperTestBase {
	
	public static class Bean {
		private String value;

		/** @return the value */
		public String getValue() {
			return value;
		}

		/** @param value the value to set */
		public void setValue(String value) {
			this.value = value;
		}
	}
	
    public interface Mapper1 {
    	@ResultMap(auto = true, mappings = {
                @Property(value = "value", columnIndex = 1) })
        @Call("{call myConcat(${1}, ${2})}")
    	Bean concat(String a, String b);
    	
    	@Call("{call myConcat(${1}, ${2})}")
    	String concat2(String a, String b);
    }
    
    @Test
    /**
     * Test calling function
     */
    public void testCall1() throws Exception {
        Mapper1 mapper = factory.createMapper(Mapper1.class);
        
        Assert.assertEquals("testtest", mapper.concat("test", "test").getValue());
    }
    
    @Test
    /**
     * Test calling function, result is primitive
     */
    public void testCall2() throws Exception {
        Mapper1 mapper = factory.createMapper(Mapper1.class);
        
        Assert.assertEquals("testtest", mapper.concat2("test", "test"));
    }
}
