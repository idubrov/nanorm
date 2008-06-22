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

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.google.code.nanorm.exceptions.IntrospectionException;
import com.google.code.nanorm.internal.introspect.AbstractIntrospectionFactory;
import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;
import com.google.code.nanorm.internal.introspect.PropertyNavigator;
import com.google.code.nanorm.internal.introspect.Setter;
import com.google.code.nanorm.internal.introspect.TypeOracle;

/**
 * ASM based {@link IntrospectionFactory} implementation. Uses runtime code
 * generation to create getters and setters.
 * 
 * TODO: Debugging.
 * 
 * @author Ivan Dubrov
 * @version 1.0 19.06.2008
 */
public class ASMIntrospectionFactory extends AbstractIntrospectionFactory implements
        IntrospectionFactory {

    private ASMClassLoader classLoader;

    private Map<AccessorKey, Object> getters;

    private Map<AccessorKey, Object> setters;

    // TODO: Synchronization!
    private final AtomicInteger counter = new AtomicInteger(0);

    private final Object lock = new Object();

    /**
     * 
     */
    public ASMIntrospectionFactory(ClassLoader parentLoader) {
        classLoader = new ASMClassLoader(parentLoader);
        getters = new ConcurrentHashMap<AccessorKey, Object>();
        setters = new ConcurrentHashMap<AccessorKey, Object>();
    }

    /**
     * {@inheritDoc}
     */
    public Getter buildGetter(final Class<?> beanClass, final String path) {
        AccessorKey key = new AccessorKey(beanClass, path);
        Builder builder = new Builder() {
            private java.lang.reflect.Type[] resultType = new java.lang.reflect.Type[1];

            public byte[] generateCode(String name) {
                PropertyPathVisitor<byte[]> v = new GetterBuilder(name, false);
                return Utils.visitPath(path, beanClass, v, resultType);
                //return buildGetterCode(name, beanClass, path, null, resultType);
            }

            public Object generateInstance(Class<?> clazz) {

                return createGetterInstance(clazz, resultType[0]);
            }
        };
        return (Getter) checkAndGenerate(getters, key, builder);
    }

    /**
     * {@inheritDoc}
     */
    public Getter buildParameterGetter(final java.lang.reflect.Type[] types, final String path) {
        AccessorKey key = new AccessorKey(types, path);
        Builder builder = new Builder() {
            private java.lang.reflect.Type[] resultType = new java.lang.reflect.Type[1];

            public byte[] generateCode(String name) {
                PropertyPathVisitor<byte[]> v = new GetterBuilder(name, false);
                return Utils.visitPath(path, types, v, resultType);
            }

            public Object generateInstance(Class<?> clazz) {
                return createGetterInstance(clazz, resultType[0]);
            }
        };
        return (Getter) checkAndGenerate(getters, key, builder);
    }

    /**
     * {@inheritDoc}
     */
    public Setter buildSetter(final Class<?> beanClass, final String path) {
        AccessorKey key = new AccessorKey(beanClass, path);
        Builder builder = new Builder() {

            public byte[] generateCode(String name) {
                PropertyPathVisitor<byte[]> v = new GetterBuilder(name, true);
                return Utils.visitPath(path, beanClass, v, null);
                //return buildSetterCode(name, beanClass, path);
            }

            public Object generateInstance(Class<?> clazz) {
                try {
                    return clazz.newInstance();
                } catch (Exception e) {
                    throw new IntrospectionException("Failed to create accessor instance!", e);
                }
            }

        };
        return (Setter) checkAndGenerate(setters, key, builder);
    }

    protected byte[] buildGetterCode(String name, Class<?> beanClass, String path,
            java.lang.reflect.Type[] types, java.lang.reflect.Type[] resultType) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        Type owner = Type.getType("L" + name + ";");

        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, name, null, "java/lang/Object",
                new String[] {"com/google/code/nanorm/internal/introspect/Getter" });

        cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, "type",
                "Ljava/lang/reflect/Type;", null, null);

        visitConstructor(owner, cw);

        // Object getValue(Object instance);
        GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, GET_VALUE, null, null, cw);
        mg.visitCode();

        if (types == null) {
            // Regular getter
            mg.loadArg(0);
            mg.checkCast(Type.getType(beanClass));
            resultType[0] = visitPath(mg, beanClass, path, false);
        } else {
            // Parameter access
            int pos = path.indexOf('.');
            if (pos == -1) {
                pos = path.length();
            }
            String context = path.substring(0, pos);

            int parameter;
            if (context.equals(ZERO_PARAMETER_ALIAS)) {
                parameter = 0;
            } else {
                parameter = Integer.parseInt(context) - 1;
            }

            // Load parameter from array
            mg.loadArg(0);
            mg.checkCast(OBJECT_ARR_TYPE);
            mg.push(parameter);
            mg.arrayLoad(OBJECT_TYPE);

            // Either return value or proceed with property navigation
            if (pos == path.length()) {
                mg.returnValue();
                resultType[0] = types[parameter];
            } else {
                path = path.substring(pos + 1);
                // TODO: Parameter type could be not class!
                mg.checkCast(Type.getType((Class<?>) types[parameter]));
                beanClass = (Class<?>) types[parameter];
                resultType[0] = visitPath(mg, beanClass, path, false);
            }
        }
        mg.endMethod();

        // Type getType();
        Method getTypeMethod = Method.getMethod("java.lang.reflect.Type getType()");

        mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, getTypeMethod, null, null, cw);

        mg.loadThis();
        mg.getField(owner, "type", JL_REFLECT_TYPE_TYPE);
        mg.returnValue();
        mg.endMethod();

        cw.visitEnd();

        byte[] code = cw.toByteArray();
        return code;
    }

    private Getter createGetterInstance(Class<?> clazz, java.lang.reflect.Type resultType) {
        try {
            Constructor<?> ct = clazz.getConstructor(java.lang.reflect.Type.class);
            return (Getter) ct.newInstance(resultType);
        } catch (Exception e) {
            throw new IntrospectionException("Failed to create getter instance!", e);
        }
    }

    protected byte[] buildSetterCode(String name, Class<?> beanClass, String path) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, name, null, "java/lang/Object",
                new String[] {"com/google/code/nanorm/internal/introspect/Setter" });

        cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, "type",
                "Ljava/lang/reflect/Type;", null, null);

        visitConstructor(cw);

        // Object setValue(Object instance);
        GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, SET_VALUE, null, null, cw);

        mg.visitCode();

        mg.loadArg(0);
        mg.checkCast(Type.getType(beanClass));
        visitPath(mg, beanClass, path, true);
        mg.endMethod();

        cw.visitEnd();

        byte[] code = cw.toByteArray();
        return code;
    }

    private void visitConstructor(ClassVisitor cw) {
        Method m = Method.getMethod("void <init>()");

        GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, m, null, null, cw);
        mg.loadThis();
        mg.invokeConstructor(Type.getType(Object.class), m);
        mg.returnValue();
        mg.endMethod();
    }

    private void visitConstructor(Type owner, ClassVisitor cw) {
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

    protected java.lang.reflect.Type visitPath(GeneratorAdapter mg, final Class<?> beanClass,
            String path, boolean isSetter) {

        PropertyNavigator nav = new PropertyNavigator(path);
        Label npeLabel = new Label(); // NPE handling code

        // Generic type
        java.lang.reflect.Type type = beanClass;
        // Resolved raw type
        Class<?> clazz = beanClass;

        // int count = isSetter ? paths.length - 1 : paths.length;
        int pos = 0;
        while (!nav.isLast()) {
            pos = nav.getPosition();

            // Check current value is not null
            mg.dup();
            mg.push(pos); // Push current path position for better NPE
            // diagnostics
            mg.swap();
            mg.ifNull(npeLabel);
            mg.pop();

            nav.next();
            // Don't need to "get" last path part
            if (nav.isLast() && isSetter) {
                break;
            }

            // Generate access code
            if (nav.getToken() == PropertyNavigator.INDEX) {
                // TODO: Could be generic array!
                if (!clazz.isArray()) {
                    throw new IllegalArgumentException("Array expected at property "
                            + path.substring(0, pos) + "(full property is " + path
                            + "). Actual type was " + clazz);
                }
                clazz = clazz.getComponentType();
                type = clazz;

                mg.push(nav.getIndex());
                mg.arrayLoad(Type.getType(clazz));
            } else if (nav.getToken() == PropertyNavigator.PROPERTY) {

                java.lang.reflect.Method getter = findGetter(clazz, nav.getProperty());
                Method method = new Method(getter.getName(),
                        Type.getType(getter.getReturnType()), new Type[0]);

                mg.invokeVirtual(Type.getType(clazz), method);

                // Resolve the return type using the current context
                type = TypeOracle.resolve(getter.getGenericReturnType(), type);

                // Find out concrete Class instance behind the generics
                clazz = TypeOracle.resolveClass(type);

                // Need to cast, types does not match
                // TODO: We probably don't need to cast, if getter.getReturnType() is instanceof 
                // next getter class 
                if (getter.getReturnType() != clazz) {
                    mg.checkCast(Type.getType(TypeOracle.resolveClass(type)));
                }
            } else {
                // TODO: !
                throw new IllegalStateException("Unexpeted!!!!");
            }
        }
        if (isSetter) {
            // Generate access code
            if (nav.getToken() == PropertyNavigator.INDEX) {
                // TODO: Could be generic array!
                if (!clazz.isArray()) {
                    throw new IllegalArgumentException("Array expected at property "
                            + path.substring(0, pos) + "(full property is " + path
                            + "). Actual type was " + clazz);
                }

                // Push array index
                mg.push(nav.getIndex());

                // Cast parameter to required type
                Type t = Type.getType(clazz.getComponentType());
                mg.loadArg(1);
                mg.unbox(t);

                mg.arrayStore(t);
            } else if (nav.getToken() == PropertyNavigator.PROPERTY) {
                // TODO: Arrays!
                java.lang.reflect.Method setter = findSetter(clazz, nav.getProperty());
                Class<?> paramType = setter.getParameterTypes()[0];

                Type t = Type.getType(paramType);

                // Cast parameter to required type
                mg.loadArg(1);
                mg.unbox(t);

                Method method = new Method(setter.getName(), VOID_TYPE, new Type[] {t });
                mg.invokeVirtual(Type.getType(clazz), method);
            }
        }
        mg.box(Type.getType(clazz));
        mg.returnValue();

        // NPE handling code. We have position in the property path on top of
        // the stack.
        mg.visitLabel(npeLabel);
        mg.push(path);
        mg.swap();
        mg.push(0);
        mg.swap();
        mg.invokeVirtual(STRING_TYPE, SUBSTRING);
        mg.push(" property is null for " + beanClass.getName()
                + " instance (full path is owner.firstName).");
        mg.invokeVirtual(STRING_TYPE, CONCAT);
        // Now we have message on the top

        mg.newInstance(NPE_TYPE);
        mg.dupX1();
        mg.swap();
        // Now we have: msg, ex, ex
        mg.invokeConstructor(NPE_TYPE, NPE_CTOR);
        mg.throwException();
        return type;
    }

    private interface Builder {
        byte[] generateCode(String name);

        Object generateInstance(Class<?> clazz);
    }

    private Object checkAndGenerate(Map<AccessorKey, Object> cache, AccessorKey key,
            Builder builder) {
        Object instance = cache.get(key);
        if (instance == null) {
            String name = "com/google/code/nanorm/generated/Accessor" + counter.incrementAndGet();

            byte[] code = builder.generateCode(name);

            // Re-check we didn't created other instance while we were
            // generating
            synchronized (lock) {
                instance = cache.get(key);

                if (instance == null) {
                    Class<?> clazz = classLoader.defineClass(name.replace('/', '.'), code);
                    instance = builder.generateInstance(clazz);
                    cache.put(key, instance);
                }
            }
        }
        return instance;
    }

    /**
     * Classloader used for accessors.
     * 
     * @author Ivan Dubrov
     * @version 1.0 21.06.2008
     */
    private class ASMClassLoader extends ClassLoader {
        /**
         * Constructor.
         * 
         * @param parent parent classloader
         */
        public ASMClassLoader(ClassLoader parent) {
            super(parent);
        }

        /**
         * Define class in the classloader.
         * @param name class name
         * @param b code
         * @return {@link Class} instance.
         */
        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }
}
