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
import com.google.code.nanorm.test.beans.PrimitiveTypesBean;
import com.google.code.nanorm.test.common.MapperTestBase;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
@SuppressWarnings("all")
public class PrimitiveTypesResultMapTest extends MapperTestBase {
    public interface Mapper {
        @Select("SELECT * FROM primitive WHERE id = ${1}")
        PrimitiveTypesBean getPrimitiveTypes(int id);
    }

    @Test
    /**
     * Test primitive types mapping
     */
    public void testPrimitiveTypes() {
        Mapper mapper = factory.createMapper(Mapper.class);
        PrimitiveTypesBean bean = mapper.getPrimitiveTypes(1);
        
        Assert.assertEquals(37, bean.getPrimByte());
        Assert.assertEquals(-23, (byte) bean.getWrapByte());
        Assert.assertEquals(8723, bean.getPrimShort());
        Assert.assertEquals(-6532, (short) bean.getWrapShort());
        Assert.assertEquals(824756237, bean.getPrimInt());
        Assert.assertEquals(-123809163, (int) bean.getWrapInt());
        Assert.assertEquals(282347987987234987L, bean.getPrimLong());
        Assert.assertEquals(-23429879871239879L, (long) bean.getWrapLong());
        Assert.assertEquals(true, bean.isPrimBoolean());
        Assert.assertEquals(false, (boolean) bean.getWrapBoolean());
        Assert.assertEquals('a', bean.getPrimChar());
        Assert.assertEquals('H', (char) bean.getWrapChar());
        Assert.assertEquals(34.5f, bean.getPrimFloat(), 0.01f);
        Assert.assertEquals(-25.25f, (float) bean.getWrapFloat(), 0.01f);
        Assert.assertEquals(44.5, bean.getPrimDouble(), 0.01);
        Assert.assertEquals(-47.125, (double) bean.getWrapDouble(), 0.01);
        Assert.assertEquals("Hello, H2!", bean.getString());
    }
}
