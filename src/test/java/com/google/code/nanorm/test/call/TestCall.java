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

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.code.nanorm.annotations.Call;
import com.google.code.nanorm.annotations.Property;
import com.google.code.nanorm.annotations.ResultMap;
import com.google.code.nanorm.test.common.MapperTestBase;
import com.google.code.nanorm.test.common.StringHolder;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
@SuppressWarnings("all")
public class TestCall extends MapperTestBase {

    public interface Mapper1 {
        @ResultMap(auto = true, mappings = {@Property(value = "value", columnIndex = 1) })
        @Call("{call myConcat(${1}, ${2})}")
        StringHolder concat(String a, String b);

        @Call("{call myConcat(${1}, ${2})}")
        String concat2(String a, String b);

        @Call("{call myConcat2(${1}, ${2}, ${3.value,type=OUT})}")
        String concat3(String a, String b, StringHolder c);
    }

    /**
     * TEST: Invoke {@link Mapper1#concat(String, String)} method with
     * parameters.
     * 
     * EXPECT: &ldquo;Hello, World!&rdquo; is returned in {@link StringHolder}
     * instance.
     */
    @Test
    public void testCall1() throws Exception {
        Mapper1 mapper = factory.createMapper(Mapper1.class);

        Assert.assertEquals("Hello, World!", mapper.concat("Hello", ", World!").getValue());
    }

    /**
     * TEST: Invoke {@link Mapper1#concat2(String, String)} method with
     * parameters.
     * 
     * EXPECT: &ldquo;World, Hello!&rdquo; is returned.
     */
    @Test
    public void testCall2() throws Exception {
        Mapper1 mapper = factory.createMapper(Mapper1.class);

        Assert.assertEquals("World, Hello!", mapper.concat2("World", ", Hello!"));
    }

    /**
     * TEST: Invoke {@link Mapper1#concat3(String, String, StringHolder)} method
     * with parameters.
     * 
     * EXPECT: &ldquo;World, Hello!&rdquo; is returned. {@link StringHolder}
     * instance has the same in the <code>value</code> property.
     */
    @Test
    @Ignore("H2 does not support OUT parameters yet")
    public void testCall3() throws Exception {
        Mapper1 mapper = factory.createMapper(Mapper1.class);

        StringHolder bean = new StringHolder();
        Assert.assertNull(bean.getValue());
        Assert.assertEquals("World, Hello!", mapper.concat3("World", ", Hello!", bean));
        Assert.assertEquals("World, Hello!", bean.getValue());
    }
}
