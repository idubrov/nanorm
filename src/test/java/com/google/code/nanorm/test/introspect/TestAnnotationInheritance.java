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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import org.junit.Test;

import com.google.code.nanorm.annotations.Options;
import com.google.code.nanorm.annotations.Scalar;
import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.test.common.MapperTestBase;

/**
 * Check that annotations are inherited by the mapper class (only in ASM case).
 * 
 * @author Ivan Dubrov
 */
@SuppressWarnings("all")
public class TestAnnotationInheritance extends MapperTestBase {

    public enum Number {
        ONE, TWO, THREE;
    }

    @Target( {ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Ann {
        int intValue() default 0;

        String strValue() default "";

        Number[] numbersSet() default {};

        Class<?> classLiteral() default Object.class;
    }

    @Target( {ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnNested {
        Ann[] annArray();
    }

    @Ann(intValue = 10, strValue = "Hello", numbersSet = {Number.ONE, Number.TWO }, classLiteral = String.class)
    @AnnNested(annArray = {@Ann(intValue = 25) })
    public interface Mapper1 {
        @Select("SELECT id FROM core WHERE id = ${1}")
        @Scalar
        @Ann(intValue = 2, strValue = "Hi", numbersSet = {Number.THREE, Number.TWO }, classLiteral = Object.class)
        @AnnNested(annArray = {@Ann(intValue = 35) })
        int select(int id);
    }

    /**
     * Test that class annotations are copied to the mapper implementation class.
     * @throws NoSuchMethodException
     */
    @Test
    public void testClassAnnotations() throws NoSuchMethodException {
        Mapper1 mapper = factory.createMapper(Mapper1.class);
        Class<?> clazz = mapper.getClass();

        Ann ann = clazz.getAnnotation(Ann.class);
        assertNotNull(ann);
        assertEquals(10, ann.intValue());
        assertEquals("Hello", ann.strValue());
        assertArrayEquals(new Number[] {Number.ONE, Number.TWO}, ann.numbersSet());

        AnnNested ann2 = clazz.getAnnotation(AnnNested.class);
        assertNotNull(ann2);
        assertEquals(1, ann2.annArray().length);
        
        System.err.println(ann2.annArray()[0]);
        assertEquals(25, ann2.annArray()[0].intValue());
    }
    
    /**
     * Test that method annotations are copied to the mapper implementation class.
     * @throws NoSuchMethodException
     */
    @Test
    public void testMethodAnnotations() throws NoSuchMethodException {
        Mapper1 mapper = factory.createMapper(Mapper1.class);
        Class<?> clazz = mapper.getClass();
        Method method = clazz.getMethod("select", int.class);

        Ann ann = method.getAnnotation(Ann.class);
        assertNotNull(ann);
        assertEquals(2, ann.intValue());
        assertEquals("Hi", ann.strValue());
        assertArrayEquals(new Number[] {Number.THREE, Number.TWO}, ann.numbersSet());

        AnnNested ann2 = method.getAnnotation(AnnNested.class);
        assertNotNull(ann2);
        assertEquals(1, ann2.annArray().length);
        
        System.err.println(ann2.annArray()[0]);
        assertEquals(35, ann2.annArray()[0].intValue());
    }
}
