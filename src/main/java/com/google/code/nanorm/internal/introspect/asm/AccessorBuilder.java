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

package com.google.code.nanorm.internal.introspect.asm;

import static com.google.code.nanorm.internal.introspect.asm.Constants.CONCAT;
import static com.google.code.nanorm.internal.introspect.asm.Constants.CTOR;
import static com.google.code.nanorm.internal.introspect.asm.Constants.GETTER_CTOR;
import static com.google.code.nanorm.internal.introspect.asm.Constants.GET_VALUE;
import static com.google.code.nanorm.internal.introspect.asm.Constants.JL_REFLECT_TYPE_TYPE;
import static com.google.code.nanorm.internal.introspect.asm.Constants.NPE_CTOR;
import static com.google.code.nanorm.internal.introspect.asm.Constants.NPE_TYPE;
import static com.google.code.nanorm.internal.introspect.asm.Constants.OBJECT_ARR_TYPE;
import static com.google.code.nanorm.internal.introspect.asm.Constants.OBJECT_TYPE;
import static com.google.code.nanorm.internal.introspect.asm.Constants.SET_VALUE;
import static com.google.code.nanorm.internal.introspect.asm.Constants.STRING_TYPE;
import static com.google.code.nanorm.internal.introspect.asm.Constants.SUBSTRING;
import static com.google.code.nanorm.internal.introspect.asm.Constants.VOID_TYPE;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.google.code.nanorm.internal.introspect.PropertyVisitor;
import com.google.code.nanorm.internal.introspect.TypeOracle;
import com.google.code.nanorm.internal.introspect.IntrospectUtils;

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 22.06.2008
 */
public class AccessorBuilder implements PropertyVisitor<byte[]> {
    
    private ClassWriter cw;
    
    private String fullPath;
    
    private Class<?> initialBeanClass;
    
    private GeneratorAdapter gettermg;
    
    private Label npeLabel = new Label();
    
    private final boolean isSetter;
    
    public AccessorBuilder(String name, boolean isSetter) {
        this.isSetter = isSetter;
        
        cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        Type owner = Type.getType("L" + name + ";");
        if(isSetter) {
            cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, name, null, "java/lang/Object",
                    new String[] {"com/google/code/nanorm/internal/introspect/Setter" });

            visitSetterConstructor();
        } else {
            cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, name, null, "java/lang/Object",
                    new String[] {"com/google/code/nanorm/internal/introspect/Getter" });

            cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, "type",
                    "Ljava/lang/reflect/Type;", null, null);

            visitGetterConstructor(owner);
            visitGetType(owner);
        }
    }
    
    private void visitSetterConstructor() {
        Method m = Method.getMethod("void <init>()");

        GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, m, null, null, cw);
        mg.loadThis();
        mg.invokeConstructor(Type.getType(Object.class), m);
        mg.returnValue();
        mg.endMethod();
    }
    
    private void visitGetterConstructor(Type owner) {
        GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, GETTER_CTOR, null, null,
                cw);

        mg.loadThis();
        mg.invokeConstructor(OBJECT_TYPE, CTOR);
        mg.loadThis();
        mg.loadArg(0);
        mg.putField(owner, "type", JL_REFLECT_TYPE_TYPE);
        mg.returnValue();
        mg.endMethod();
    }
    
    private void visitGetType(Type owner) {
        // Type getType();
        Method getTypeMethod = Method.getMethod("java.lang.reflect.Type getType()");

        GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, getTypeMethod, null, null, cw);

        mg.loadThis();
        mg.getField(owner, "type", JL_REFLECT_TYPE_TYPE);
        mg.returnValue();
        mg.endMethod();
    }
    
    public void visitBegin(Class<?> beanClass, String path) {
        this.fullPath = path;
        this.initialBeanClass = beanClass;
        
        if(isSetter) {
            // void setValue(Object instance);
            gettermg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, SET_VALUE, null, null, cw);
        } else {
            // Object getValue(Object instance);
            gettermg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, GET_VALUE, null, null, cw);
        }
        gettermg.visitCode();

        // Regular getter/setter
        gettermg.loadArg(0);
        gettermg.checkCast(Type.getType(beanClass));
    }
    
    /**
     * {@inheritDoc}
     */
    public void visitBegin(java.lang.reflect.Type[] types, int parameter, String path) {
        this.fullPath = path;
        
        // TODO: Cast!
        this.initialBeanClass = (Class<?>) types[parameter];

        if(isSetter) {
            // void setValue(Object instance);
            gettermg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, SET_VALUE, null, null, cw);
        } else {
            // Object getValue(Object instance);
            gettermg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, GET_VALUE, null, null, cw);
        }
        gettermg.visitCode();
        
        //  Load parameter from array
        gettermg.loadArg(0);
        gettermg.checkCast(OBJECT_ARR_TYPE);
        gettermg.push(parameter);
        gettermg.arrayLoad(OBJECT_TYPE);

        // TODO: If it is end -- do not cast!
        Class<?> c = (Class<?>) types[parameter];
        if(!c.isPrimitive()) {
            gettermg.checkCast(Type.getType(c));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void visitIndex(int pos, int index, boolean isLast, Class<?> beanClass, Class<?> componentClass) {
        checkNull(pos);
        
        if(isLast && isSetter) {
            // Push array index
            gettermg.push(index);

            // Cast parameter to required type
            Type t = Type.getType(componentClass);
            gettermg.loadArg(1);
            gettermg.unbox(t);

            gettermg.arrayStore(t);
        } else {
            gettermg.push(index);
            gettermg.arrayLoad(Type.getType(componentClass));
        
            if(isLast) {
                gettermg.box(Type.getType(componentClass));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void visitProperty(int pos, String property, java.lang.reflect.Method getter,
            boolean isLast, Class<?> beanClass, Class<?> propClass) {
        checkNull(pos);
        
        if(isLast && isSetter) {
            java.lang.reflect.Method setter = IntrospectUtils.findSetter(beanClass, property);
            Class<?> paramType = setter.getParameterTypes()[0];

            Type t = Type.getType(paramType);
            
            // Cast parameter to required type
            gettermg.loadArg(1);
            gettermg.unbox(t);

            Method method = new Method(setter.getName(), VOID_TYPE, new Type[] {t });
            gettermg.invokeVirtual(Type.getType(beanClass), method);
        } else {
        
            Method method = new Method(getter.getName(),
                    Type.getType(getter.getReturnType()), new Type[0]);
            
            gettermg.invokeVirtual(Type.getType(beanClass), method);
            
            // Need to cast, types does not match
            // TODO: We probably don't need to cast, if getter.getReturnType() is instanceof 
            // next getter class 
            if (getter.getReturnType() != propClass) {
                gettermg.checkCast(Type.getType(propClass));
            }
            
            if(isLast) {
                gettermg.box(Type.getType(propClass));
            }
        }
    }
    
    protected void checkNull(int pos) {
     // Check current value is not null
        gettermg.dup();
        gettermg.push(pos); // Push current path position for better NPE
        // diagnostics
        gettermg.swap();
        gettermg.ifNull(npeLabel);
        gettermg.pop();
    }
    
    protected void npeHandler() {
        // NPE handling code. We have position in the property path on top of
        // the stack.
        gettermg.visitLabel(npeLabel);
        gettermg.push(fullPath);
        gettermg.swap();
        gettermg.push(0);
        gettermg.swap();
        gettermg.invokeVirtual(STRING_TYPE, SUBSTRING);
        gettermg.push(" property is null for " + initialBeanClass.getName()
                + " instance (full path is owner.firstName).");
        gettermg.invokeVirtual(STRING_TYPE, CONCAT);
        // Now we have message on the top

        gettermg.newInstance(NPE_TYPE);
        gettermg.dupX1();
        gettermg.swap();
        // Now we have: msg, ex, ex
        
        gettermg.invokeConstructor(NPE_TYPE, NPE_CTOR);
        gettermg.throwException();
    }
    
    /**
     * {@inheritDoc}
     */
    public byte[] visitEnd() {
        gettermg.returnValue();
        npeHandler();
        
        gettermg.endMethod();
        cw.visitEnd();
        
        byte[] code = cw.toByteArray();
        return code;
    }
}