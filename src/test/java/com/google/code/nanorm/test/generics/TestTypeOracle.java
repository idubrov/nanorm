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

package com.google.code.nanorm.test.generics;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.junit.Assert;
import org.junit.Test;

import com.google.code.nanorm.internal.introspect.TypeOracle;
import com.google.code.nanorm.test.beans.Publication;

@SuppressWarnings("all")
public class TestTypeOracle {

    public interface Mapper1<S, T> {
        T method(S id);
    }

    public interface Mapper2 extends Mapper1<Integer, Publication> {
        Publication method(Integer id);
    }

    @Test
    /**
     * Test resolving method parameters.
     */
    public void testMethodParameters() throws Exception {
        Method m = Mapper1.class.getDeclaredMethod("method", Object.class);
        Assert.assertNotNull(m);

        Type[] params = TypeOracle.resolveMethodArguments(m, Mapper2.class);

        Assert.assertEquals(1, params.length);
        Assert.assertEquals(Integer.class, params[0]);
    }

    public interface Mapper3<S, T> {
        T method(S id);
    }

    public interface Mapper4<S, T> extends Mapper3<S, T> {
        // Nothing...
    }

    public interface Mapper5 extends Mapper4<Integer, Publication> {
        Publication method(Integer id);
    }

    @Test
    /**
     * Test resolving method parameters with longer hierarchy.
     */
    public void testMethodParameters2() throws Exception {
        Method m = Mapper3.class.getDeclaredMethod("method", Object.class);
        Assert.assertNotNull(m);

        Type[] params = TypeOracle.resolveMethodArguments(m, Mapper5.class);

        Assert.assertEquals(1, params.length);
        Assert.assertEquals(Integer.class, params[0]);
    }
}
