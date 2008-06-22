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

package com.google.code.nanorm.test;

import static com.google.code.nanorm.internal.introspect.PropertyNavigator.INDEX;
import static com.google.code.nanorm.internal.introspect.PropertyNavigator.PROPERTY;

import org.junit.Assert;
import org.junit.Test;

import com.google.code.nanorm.internal.introspect.PropertyNavigator;

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 21.06.2008
 */
public class TestPropNavigator {

    @Test
    public void testPath1() {
        PropertyNavigator nav = new PropertyNavigator("prop1.prop2.prop3");
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(PROPERTY, nav.next());
        Assert.assertEquals("prop1", nav.getProperty());
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(PROPERTY, nav.next());
        Assert.assertEquals("prop2", nav.getProperty());
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(PROPERTY, nav.next());
        Assert.assertEquals("prop3", nav.getProperty());
        
        Assert.assertTrue(nav.hasNext());
    }
    
    @Test
    public void testPath2() {
        PropertyNavigator nav = new PropertyNavigator("prop1.prop2[5].prop3");
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(PROPERTY, nav.next());
        Assert.assertEquals("prop1", nav.getProperty());
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(PROPERTY, nav.next());
        Assert.assertEquals("prop2", nav.getProperty());
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(INDEX, nav.next());
        Assert.assertEquals(5, nav.getIndex());
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(PROPERTY, nav.next());
        Assert.assertEquals("prop3", nav.getProperty());
        
        Assert.assertTrue(nav.hasNext());
    }
    
    @Test
    public void testPath3() {
        PropertyNavigator nav = new PropertyNavigator("prop1.prop2.prop3[7]");
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(PROPERTY, nav.next());
        Assert.assertEquals("prop1", nav.getProperty());
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(PROPERTY, nav.next());
        Assert.assertEquals("prop2", nav.getProperty());
        
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(PROPERTY, nav.next());
        Assert.assertEquals("prop3", nav.getProperty());
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(INDEX, nav.next());
        Assert.assertEquals(7, nav.getIndex());
        
        Assert.assertTrue(nav.hasNext());
    }
    
    @Test
    public void testPath4() {
        PropertyNavigator nav = new PropertyNavigator("[2].prop1.prop2.prop3");
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(INDEX, nav.next());
        Assert.assertEquals(2, nav.getIndex());
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(PROPERTY, nav.next());
        Assert.assertEquals("prop1", nav.getProperty());
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(PROPERTY, nav.next());
        Assert.assertEquals("prop2", nav.getProperty());
        
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(PROPERTY, nav.next());
        Assert.assertEquals("prop3", nav.getProperty());
        
        Assert.assertTrue(nav.hasNext());
    }
    
    @Test
    public void testPath5() {
        PropertyNavigator nav = new PropertyNavigator("prop1.prop2[5a].prop3");
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(PROPERTY, nav.next());
        Assert.assertEquals("prop1", nav.getProperty());
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(PROPERTY, nav.next());
        Assert.assertEquals("prop2", nav.getProperty());
        
        Assert.assertFalse(nav.hasNext());
        try {
            nav.next();
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // Nothing.
        }
    }
    
    @Test
    public void testPath6() {
        PropertyNavigator nav = new PropertyNavigator("prop1.prop2[5]b.prop3");
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(PROPERTY, nav.next());
        Assert.assertEquals("prop1", nav.getProperty());
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(PROPERTY, nav.next());
        Assert.assertEquals("prop2", nav.getProperty());
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(INDEX, nav.next());
        Assert.assertEquals(5, nav.getIndex());
        
        Assert.assertFalse(nav.hasNext());
        try {
            nav.next();
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // Nothing.
        }
    }
    
    @Test
    public void testPath7() {
        PropertyNavigator nav = new PropertyNavigator("prop1.prop2]5].prop3");
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(PROPERTY, nav.next());
        Assert.assertEquals("prop1", nav.getProperty());
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(PROPERTY, nav.next());
        Assert.assertEquals("prop2", nav.getProperty());
        
        Assert.assertFalse(nav.hasNext());
        try {
            nav.next();
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // Nothing.
        }
    }
    
    @Test
    public void testPath8() {
        PropertyNavigator nav = new PropertyNavigator("prop1.prop2.^prop3");
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(PROPERTY, nav.next());
        Assert.assertEquals("prop1", nav.getProperty());
        
        Assert.assertFalse(nav.hasNext());
        Assert.assertEquals(PROPERTY, nav.next());
        Assert.assertEquals("prop2", nav.getProperty());
        
        Assert.assertFalse(nav.hasNext());
        try {
            nav.next();
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // Nothing.
        }
    }
}
