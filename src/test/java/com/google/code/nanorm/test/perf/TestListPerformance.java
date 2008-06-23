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

package com.google.code.nanorm.test.perf;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.test.beans.PrimitiveTypesBean;
import com.google.code.nanorm.test.common.MapperTestBase;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import com.ibatis.sqlmap.client.SqlMapSession;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 19.06.2008
 */
@Ignore
public class TestListPerformance extends MapperTestBase {

    public interface Mapper {
        @Select("SELECT * FROM primitive WHERE id = ${1}")
        PrimitiveTypesBean getPrimitiveTypes(int id) throws Exception;
    }

    @Test
    public void testPerformance() throws Exception {
        Mapper mapper1 = new MapperImpl();
        Mapper mapper2 = factory.createMapper(Mapper.class);

        SqlMapClient client = 
            SqlMapClientBuilder.buildSqlMapClient(TestListPerformance.class.getResourceAsStream("sql-map-config.xml"));
        
        

        check(mapper1.getPrimitiveTypes(1));
        check(mapper2.getPrimitiveTypes(1));
        
        SqlMapSession session = client.openSession(conn);
        check((PrimitiveTypesBean) session.queryForObject("selectPrimitiveBean", 1));
        
        for (int i = 0; i < 10000; ++i) {
            mapper1.getPrimitiveTypes(1);
            mapper2.getPrimitiveTypes(1);
        }

        long t1 = System.currentTimeMillis();
        for (int i = 0; i < 100000; ++i) {
            mapper1.getPrimitiveTypes(1);
        }
        long t2 = System.currentTimeMillis();

        long t5 = System.currentTimeMillis();
        for (int i = 0; i < 100000; ++i) {
            session.queryForObject("selectPrimitiveBean", 1);
        }
        long t6 = System.currentTimeMillis();

        
        long t3 = System.currentTimeMillis();
        for (int i = 0; i < 100000; ++i) {
            mapper2.getPrimitiveTypes(1);
        }
        long t4 = System.currentTimeMillis();
        
        

        System.err.println("R: " + (t2 - t1) + " vs " + (t4 - t3) + " vs " + (t6 - t5));
        System.err.println((double) (t4 - t3) / (t2 - t1));
        System.err.println((double) (t6 - t5) / (t4 - t3));
        
        System.err.println((double) ((t6 - t5) - (t2 - t1)) / ((t4 - t3) - (t2 - t1)));
    }

    private class MapperImpl implements Mapper {

        public PrimitiveTypesBean getPrimitiveTypes(int id) throws Exception {
            PrimitiveTypesBean bean = new PrimitiveTypesBean();

            PreparedStatement ps = conn.prepareStatement("SELECT * FROM primitive WHERE id = ?");
            try {
                ps.setInt(1, id);

                ResultSet rs = ps.executeQuery();
                try {
                    if (rs.next()) {
                        bean.setPrimByte(rs.getByte("primByte"));
                        bean.setWrapByte(rs.getByte("wrapByte"));

                        bean.setPrimShort(rs.getShort("primShort"));
                        bean.setWrapShort(rs.getShort("wrapShort"));

                        bean.setPrimInt(rs.getInt("primInt"));
                        bean.setWrapInt(rs.getInt("wrapInt"));

                        bean.setPrimLong(rs.getLong("primLong"));
                        bean.setWrapLong(rs.getLong("wrapLong"));

                        bean.setPrimBoolean(rs.getBoolean("primBoolean"));
                        bean.setWrapBoolean(rs.getBoolean("wrapBoolean"));

                        bean.setPrimChar(rs.getString("primChar").charAt(0));
                        bean.setWrapChar(rs.getString("wrapChar").charAt(0));

                        bean.setPrimFloat(rs.getFloat("primFloat"));
                        bean.setWrapFloat(rs.getFloat("wrapFloat"));

                        bean.setPrimDouble(rs.getDouble("primDouble"));
                        bean.setWrapDouble(rs.getDouble("wrapDouble"));

                        bean.setString(rs.getString("string"));
                    }
                } finally {
                    rs.close();
                }
            } finally {
                ps.close();
            }
            return bean;
        }

    }

    public void check(PrimitiveTypesBean bean) {
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
