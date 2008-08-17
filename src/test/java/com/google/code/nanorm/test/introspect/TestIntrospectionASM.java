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

package com.google.code.nanorm.test.introspect;

import org.junit.Assert;
import org.junit.Test;

import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;
import com.google.code.nanorm.internal.introspect.Setter;
import com.google.code.nanorm.internal.introspect.asm.ASMIntrospectionFactory;
import com.google.code.nanorm.test.beans.Publication;

/**
 * ASM introspection factory test. Extends base class which contains all tests
 * that are not specific to concrete factory.
 * 
 * @author Ivan Dubrov
 * @version 1.0 22.06.2008
 */
public class TestIntrospectionASM extends TestIntrospectionBase {

	/**
	 * {@inheritDoc}
	 */
    protected IntrospectionFactory provideIntrospectionFactory() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return new ASMIntrospectionFactory(cl);
    }

    /**
     * Test {@link NullPointerException} handling for getter.
     */
    @Test
    public void testGetterNPE() {
        Publication pub = new Publication();
        pub.setArticle(null);
        Getter getter = factory.buildGetter(Publication.class, "article.subject");

        try {
            getter.getValue(pub);
        } catch (NullPointerException e) {
            Assert.assertEquals("article property is null for "
                    + "com.google.code.nanorm.test.beans.Publication "
                    + "instance (full path is article.subject).", e.getMessage());
        }
    }

    /**
     * Test {@link NullPointerException} handling for setter.
     */
    @Test
    public void testSetterNPE() {
        Publication pub = new Publication();
        pub.setArticle(null);
        Setter setter = factory.buildSetter(Publication.class, "article.subject");

        try {
            setter.setValue(pub, "World Domination");
        } catch (NullPointerException e) {
            Assert.assertEquals("article property is null for "
                    + "com.google.code.nanorm.test.beans.Publication "
                    + "instance (full path is article.subject).", e.getMessage());
        }
    }
}
