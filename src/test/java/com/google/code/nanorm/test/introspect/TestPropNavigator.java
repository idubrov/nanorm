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

package com.google.code.nanorm.test.introspect;

import static com.google.code.nanorm.internal.introspect.PropertyNavigator.INDEX;
import static com.google.code.nanorm.internal.introspect.PropertyNavigator.PROPERTY;

import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Test;

import com.google.code.nanorm.internal.introspect.PropertyNavigator;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 21.06.2008
 */
public class TestPropNavigator {

    /**
     * TEST: Create proprety navigators.
     * 
     * EXPECT: Validate getters return expected values.
     */
    @Test
    public void testPropertyNavigator() {
        PropertyNavigator nav = new PropertyNavigator("prop1.prop2");
        Assert.assertEquals("prop1.prop2", nav.getPath());
        Assert.assertEquals(0, nav.getPosition());

        nav = new PropertyNavigator("prop2.prop3", 6);
        Assert.assertEquals("prop2.prop3", nav.getPath());
        Assert.assertEquals(6, nav.getPosition());
    }

    /**
     * TEST: Navigate property &ldquo;prop1.prop2.prop3&rdquo; up to the end.
     * 
     * EXPECT: First &ldquo;prop1&rdquo;, then &ldquo;prop2&rdquo; and finally
     * &ldquo;prop3&rdquo; are parsed. The {@link PropertyNavigator#hasNext()}
     * returns <code>false</code> after this.
     */
    @Test
    public void testPath1() {
        PropertyNavigator nav = new PropertyNavigator("prop1.prop2.prop3");

        assertProperty(nav, "prop1");
        assertProperty(nav, "prop2");
        assertProperty(nav, "prop3");

        Assert.assertFalse(nav.hasNext());
    }

    /**
     * TEST: Navigate property &ldquo;prop1.prop2[5].prop3&rdquo; up to the end.
     * 
     * EXPECT: First &ldquo;prop1&rdquo;, then &ldquo;prop2&rdquo;, then index
     * &ldquo;5&rdquo; and finally &ldquo;prop3&rdquo; are parsed. The
     * {@link PropertyNavigator#hasNext()} returns <code>false</code> after
     * this.
     */
    @Test
    public void testPath2() {
        PropertyNavigator nav = new PropertyNavigator("prop1.prop2[5].prop3");

        assertProperty(nav, "prop1");
        assertProperty(nav, "prop2");
        assertIndex(nav, 5);
        assertProperty(nav, "prop3");

        Assert.assertFalse(nav.hasNext());
    }

    /**
     * TEST: Navigate property &ldquo;prop1.prop2.prop3[7]&rdquo; up to the end.
     * 
     * EXPECT: First &ldquo;prop1&rdquo;, then &ldquo;prop2&rdquo;, then
     * &ldquo;prop3&rdquo; and finally index &ldquo;7&rdquo; are parsed. The
     * {@link PropertyNavigator#hasNext()} returns <code>false</code> after
     * this.
     */
    @Test
    public void testPath3() {
        PropertyNavigator nav = new PropertyNavigator("prop1.prop2.prop3[7]");

        assertProperty(nav, "prop1");
        assertProperty(nav, "prop2");
        assertProperty(nav, "prop3");
        assertIndex(nav, 7);

        Assert.assertFalse(nav.hasNext());
    }

    /**
     * TEST: Navigate property &ldquo;prop1.prop2.7.prop3&rdquo; up to the end.
     * 
     * EXPECT: First &ldquo;prop1&rdquo;, then &ldquo;prop2&rdquo;, then index
     * &ldquo;7&rdquo; and finally &ldquo;prop3&rdquo; are parsed. The
     * {@link PropertyNavigator#hasNext()} returns <code>false</code> after
     * this.
     */
    @Test
    public void testPath4() {
        PropertyNavigator nav = new PropertyNavigator("prop1.prop2.7.prop3");

        assertProperty(nav, "prop1");
        assertProperty(nav, "prop2");
        assertIndex(nav, 7);
        assertProperty(nav, "prop3");

        Assert.assertFalse(nav.hasNext());
    }

    /**
     * TEST: Navigate property &ldquo;prop1.prop2.7.prop3&rdquo; up to the end.
     * 
     * EXPECT: First &ldquo;prop1&rdquo;, then &ldquo;prop2&rdquo;, then
     * &ldquo;prop3&rdquo; and finally index &ldquo;9&rdquo; are parsed. The
     * {@link PropertyNavigator#hasNext()} returns <code>false</code> after
     * this.
     */
    @Test
    public void testPath5() {
        PropertyNavigator nav = new PropertyNavigator("prop1.prop2.prop3.9");

        assertProperty(nav, "prop1");
        assertProperty(nav, "prop2");
        assertProperty(nav, "prop3");
        assertIndex(nav, 9);

        Assert.assertFalse(nav.hasNext());
    }

    /**
     * TEST: Navigate property &ldquo;[2].prop1.prop2.prop3&rdquo; up to the
     * end.
     * 
     * EXPECT: First index &ldquo;2&rdquo;, then &ldquo;prop1&rdquo;, then
     * &ldquo;prop2&rdquo; and finally &ldquo;prop3&rdquo; are parsed. The
     * {@link PropertyNavigator#hasNext()} returns <code>false</code> after
     * this.
     */
    @Test
    public void testPath6() {
        PropertyNavigator nav = new PropertyNavigator("[2].prop1.prop2.prop3");

        assertIndex(nav, 2);
        assertProperty(nav, "prop1");
        assertProperty(nav, "prop2");
        assertProperty(nav, "prop3");

        Assert.assertFalse(nav.hasNext());
    }

    /**
     * TEST: Navigate property &ldquo;prop1.prop2[5a].prop3&rdquo; up to the
     * end.
     * 
     * EXPECT: First &ldquo;prop1&rdquo;, then &ldquo;prop2&rdquo; are parsed.
     * Then exception is thrown due to invalid property path.
     */
    @Test
    public void testPath7() {
        PropertyNavigator nav = new PropertyNavigator("prop1.prop2[5a].prop3");

        assertProperty(nav, "prop1");
        assertProperty(nav, "prop2");

        Assert.assertTrue(nav.hasNext());
        try {
            nav.next();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // Nothing.
        }
    }

    /**
     * TEST: Navigate property &ldquo;prop1.prop2[5a].prop3&rdquo; up to the
     * end.
     * 
     * EXPECT: First &ldquo;prop1&rdquo;, then &ldquo;prop2&rdquo; and finally
     * index &ldquo;5&rdquo; are parsed. Then exception is thrown due to invalid
     * property path.
     */
    @Test
    public void testPath8() {
        PropertyNavigator nav = new PropertyNavigator("prop1.prop2[5]b.prop3");

        assertProperty(nav, "prop1");
        assertProperty(nav, "prop2");
        assertIndex(nav, 5);

        Assert.assertTrue(nav.hasNext());
        try {
            nav.next();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // Nothing.
        }
    }

    /**
     * TEST: Navigate property &ldquo;prop1.prop2]5].prop3&rdquo; up to the end.
     * 
     * EXPECT: First &ldquo;prop1&rdquo;, then &ldquo;prop2&rdquo; are parsed.
     * Then exception is thrown due to invalid property path.
     */
    @Test
    public void testPath9() {
        PropertyNavigator nav = new PropertyNavigator("prop1.prop2]5].prop3");

        assertProperty(nav, "prop1");
        assertProperty(nav, "prop2");

        Assert.assertTrue(nav.hasNext());
        try {
            nav.next();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // Nothing.
        }
    }

    /**
     * TEST: Navigate property &ldquo;prop1.prop2[5&rdquo; up to the end.
     * 
     * EXPECT: First &ldquo;prop1&rdquo;, then &ldquo;prop2&rdquo; are parsed.
     * Then exception is thrown due to invalid property path.
     */
    @Test
    public void testPath10() {
        PropertyNavigator nav = new PropertyNavigator("prop1.prop2[5");

        assertProperty(nav, "prop1");
        assertProperty(nav, "prop2");

        Assert.assertTrue(nav.hasNext());
        try {
            nav.next();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // Nothing.
        }
    }

    /**
     * TEST: Navigate property &ldquo;prop1.prop2.^prop3&rdquo; up to the end.
     * 
     * EXPECT: First &ldquo;prop1&rdquo;, then &ldquo;prop2&rdquo; are parsed.
     * Then exception is thrown due to invalid property path.
     */
    @Test
    public void testPath11() {
        PropertyNavigator nav = new PropertyNavigator("prop1.prop2.^prop3");

        assertProperty(nav, "prop1");
        assertProperty(nav, "prop2");

        Assert.assertTrue(nav.hasNext());
        try {
            nav.next();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // Nothing.
        }
    }

    /**
     * TEST: Navigate property &ldquo;prop1.prop2&rdquo; up to the end. Try to
     * retrieve next element after {@link PropertyNavigator#hasNext()} returned
     * <code>false</code>.
     * 
     * EXPECT: First &ldquo;prop1&rdquo;, then &ldquo;prop2&rdquo; are parsed.
     * Then {@link NoSuchElementException} is thrown due to invalid property
     * path.
     */
    @Test
    public void testPath12() {
        PropertyNavigator nav = new PropertyNavigator("prop1.prop2");

        assertProperty(nav, "prop1");
        assertProperty(nav, "prop2");

        Assert.assertFalse(nav.hasNext());
        try {
            nav.next();
            Assert.fail();
        } catch (NoSuchElementException e) {
            // Nothing.
        }
    }

    /**
     * Helper method to validate that property navigator has more elements in
     * the path, then navigate the path and validate that next element has type
     * of <code>PROPERTY</code>. Finally, validate the property name.
     * 
     * @param nav property path
     * @param expected property name
     */
    private void assertProperty(PropertyNavigator nav, String expected) {
        Assert.assertTrue(nav.hasNext());
        Assert.assertEquals(PROPERTY, nav.next());
        Assert.assertEquals(PROPERTY, nav.getElementType());
        Assert.assertEquals(expected, nav.getProperty());
    }

    /**
     * Helper method to validate that property navigator has more elements in
     * the path, then navigate the path and validate that next element has type
     * of <code>INDEX</code>. Finally, validate that index equal to the
     * expected.
     * 
     * @param nav property path
     * @param expected expected index
     */
    private void assertIndex(PropertyNavigator nav, int expected) {
        Assert.assertTrue(nav.hasNext());
        Assert.assertEquals(INDEX, nav.next());
        Assert.assertEquals(INDEX, nav.getElementType());
        Assert.assertEquals(expected, nav.getIndex());
    }
}
