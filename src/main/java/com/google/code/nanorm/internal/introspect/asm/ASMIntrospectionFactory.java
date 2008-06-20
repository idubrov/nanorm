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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.google.code.nanorm.internal.introspect.Getter;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;
import com.google.code.nanorm.internal.introspect.Setter;

/**
 * 
 * @author Ivan Dubrov
 * @version 1.0 19.06.2008
 */
public class ASMIntrospectionFactory implements IntrospectionFactory {

    private ASMClassLoader classLoader;

    private Map<AccessorKey, Getter> getters;

    private Map<AccessorKey, Setter> setters;

    private final static Method GETTER_CTOR = Method
            .getMethod("void <init>(java.lang.reflect.Type)");

    private final static Method GET_VALUE = Method
            .getMethod("java.lang.Object getValue(java.lang.Object)");

    private final static Method SET_VALUE = Method
            .getMethod("void setValue(java.lang.Object, java.lang.Object)");

    private final static Method CTOR = Method.getMethod("void <init>()");

    private final static Type JL_REFLECT_TYPE_TYPE = Type.getType(java.lang.reflect.Type.class);

    private final static Type OBJECT_TYPE = Type.getType(Object.class);

    private final static Type OBJECT_ARR_TYPE = Type.getType(Object[].class);

    // TODO: Synchronization!
    private int num = 0;

    /**
     * 
     */
    public ASMIntrospectionFactory(ClassLoader parentLoader) {
        classLoader = new ASMClassLoader(parentLoader);
        getters = new HashMap<AccessorKey, Getter>();
        setters = new HashMap<AccessorKey, Setter>();
    }

    /**
     * {@inheritDoc}
     */
    public Getter buildGetter(Class<?> beanClass, String path) {
        try {
            AccessorKey key = new AccessorKey(beanClass, path);
            Getter getter = getters.get(key);
            if (getter == null) {
                getter = buildGetterClass(beanClass, path, null);
                getters.put(key, getter);
            }
            return getter;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("!", e);
        }

    }

    protected Getter buildGetterClass(Class<?> beanClass, String path,
            java.lang.reflect.Type[] types) throws NoSuchMethodException,
            IllegalArgumentException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        // cw.visit(version, access, name, signature, superName, interfaces)

        String name = "com/google/code/nanorm/generated/Getter" + num++;
        Type owner = Type.getType("L" + name + ";");

        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, name, null, "java/lang/Object",
                new String[] {"com/google/code/nanorm/internal/introspect/Getter" });

        cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, "type",
                "Ljava/lang/reflect/Type;", null, null);

        visitConstructor(owner, cw);

        // Object getValue(Object instance);
        GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, GET_VALUE, null, null, cw);
        mg.visitCode();

        // Regular getter
        java.lang.reflect.Type resultType;
        if (types == null) {
            mg.loadArg(0);
            mg.checkCast(Type.getType(beanClass));
            resultType = visitPath(mg, beanClass, path, false);
        } else {
            int pos = path.indexOf('.');
            if (pos == -1) {
                pos = path.length();
            }
            String context = path.substring(0, pos);

            int parameter;
            if (context.equals("value")) {
                parameter = 0;
            } else {
                parameter = Integer.parseInt(context) - 1;
            }

            // Load parameter from array
            mg.loadArg(0);
            mg.checkCast(OBJECT_ARR_TYPE);
            mg.push(parameter);
            mg.arrayLoad(OBJECT_TYPE);

            if (pos == path.length()) {
                mg.returnValue();
                resultType = types[parameter];
            } else {
                String subpath = path.substring(pos + 1);
                mg.checkCast(Type.getType((Class<?>) types[parameter]));
                resultType = visitPath(mg, (Class<?>) types[parameter], subpath, false);
            }

        }
        mg.endMethod();

        // Type getType();
        Method getTypeMethod = Method.getMethod("java.lang.reflect.Type getType()");

        Type type = Type.getType(java.lang.reflect.Type.class);

        GeneratorAdapter getTypeGA = new GeneratorAdapter(Opcodes.ACC_PUBLIC, getTypeMethod,
                null, null, cw);

        getTypeGA.loadThis();
        getTypeGA.getField(owner, "type", type);
        getTypeGA.returnValue();
        getTypeGA.endMethod();

        cw.visitEnd();

        byte[] code = cw.toByteArray();
        return createInstance(name, code, resultType);
    }

    private Getter createInstance(String name, byte[] code, java.lang.reflect.Type resultType)
            throws SecurityException, NoSuchMethodException, IllegalArgumentException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<?> clazz = classLoader.defineClass(name.replace('/', '.'), code);
        Constructor<?> ct = clazz.getConstructor(java.lang.reflect.Type.class);
        return (Getter) ct.newInstance(resultType);
    }

    // private String getWrapper(Class<?> c) {
    // if (c == Integer.TYPE) {
    // return "java/lang/Integer";
    // } else if (c == Void.TYPE) {
    // return "java/lang/Void";
    // } else if (c == Boolean.TYPE) {
    // return "java/lang/Boolean";
    // } else if (c == Byte.TYPE) {
    // return "java/lang/Byte";
    // } else if (c == Character.TYPE) {
    // return "java/lang/Character";
    // } else if (c == Short.TYPE) {
    // return "java/lang/Short";
    // } else if (c == Double.TYPE) {
    // return "java/lang/Double";
    // } else if (c == Float.TYPE) {
    // return "java/lang/Float";
    // } else /* if (c == Long.TYPE) */{
    // return "java/lang/Long";
    // }
    // }

    protected Setter buildSetterClass(Class<?> beanClass, String path)
            throws NoSuchMethodException, IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        String name = "com/google/code/nanorm/generated/Setter" + num++;
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
        Class<?> clazz = classLoader.defineClass(name.replace('/', '.'), code);
        return (Setter) clazz.newInstance();
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

    private String nameOf(Class<?> clazz) {
        return clazz.getName().replace('.', '/');
    }

    protected java.lang.reflect.Type visitPath(GeneratorAdapter mg, final Class<?> beanClass,
            String property, boolean isSetter) throws NoSuchMethodException {
        String[] paths = property.split("\\.");
        Label[] labels = new Label[paths.length];

        java.lang.reflect.Type type = beanClass;
        int count = isSetter ? paths.length - 1 : paths.length;
        for (int i = 0; i < count; ++i) {
            // TODO: Cast!
            Class<?> clazz = (Class<?>) type;
            labels[i] = new Label();

            // Check not null
            mg.dup();
            mg.ifNull(labels[i]);

            // Generate access code
            java.lang.reflect.Method getter = findGetter(clazz, paths[i]);
            Method method = new Method(getter.getName(), Type.getType(getter.getReturnType()),
                    new Type[0]);
            mg.invokeVirtual(Type.getType(clazz), method);

            type = getter.getGenericReturnType();
        }
        if (isSetter) {
            // TODO: Cast!
            Class<?> clazz = (Class<?>) type;

            // Generate access code
            java.lang.reflect.Method setter = findSetter(clazz, paths[paths.length - 1]);
            String owner = clazz.getName().replace('.', '/');
            Class<?> paramType = setter.getParameterTypes()[0];
            String desc;

            org.objectweb.asm.Type t = org.objectweb.asm.Type.getType(paramType);
            desc = "(" + t.getDescriptor() + ")V";

            // Cast parameter to required type
            mg.visitIntInsn(Opcodes.ALOAD, 2);
            castTo(mg, paramType);

            mg.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner, setter.getName(), desc);
            mg.visitInsn(Opcodes.RETURN);
        } else {

            // TODO: Primitive types!
            mg.visitInsn(Opcodes.ARETURN);
        }

        // NPE handling code
        StringBuilder nullPropPath = new StringBuilder();
        for (int i = 0; i < count; ++i) {
            mg.visitLabel(labels[i]);
            mg.visitTypeInsn(Opcodes.NEW, nameOf(NullPointerException.class));
            mg.visitInsn(Opcodes.DUP);
            mg.visitLdcInsn(nullPropPath + " property is null for " + beanClass.getName()
                    + " instance.");
            mg.visitMethodInsn(Opcodes.INVOKESPECIAL, nameOf(NullPointerException.class),
                    "<init>", "(Ljava/lang/String;)V");
            mg.visitInsn(Opcodes.ATHROW);

            if (i != 0) {
                nullPropPath.append('.');
            }
            nullPropPath.append(paths[i]);
        }
        // Final property type
        return type;
    }

    private void castTo(MethodVisitor mv, Class<?> c) {
        if (!c.isPrimitive()) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, nameOf(c));
        } else {
            if (c == Integer.TYPE) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
            } else if (c == Void.TYPE) {
                mv.visitInsn(Opcodes.POP);
                mv.visitInsn(Opcodes.ACONST_NULL);
            } else if (c == Boolean.TYPE) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Boolean");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue",
                        "()Z");
            } else if (c == Byte.TYPE) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Byte");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
            } else if (c == Character.TYPE) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Character");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue",
                        "()C");
            } else if (c == Short.TYPE) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Short");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
            } else if (c == Double.TYPE) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Double");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue",
                        "()D");
            } else if (c == Float.TYPE) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Float");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
            } else /* if (c == Long.TYPE) */{
                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Long");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
            }
        }
    }

    private java.lang.reflect.Method findGetter(Class<?> clazz, String property) {
        try {
            String name = "get" + Character.toUpperCase(property.charAt(0))
                    + property.substring(1);
            try {
                return clazz.getMethod(name);
            } catch (NoSuchMethodException e) {
                // TODO: Hack!
                name = "is" + Character.toUpperCase(property.charAt(0)) + property.substring(1);
                return clazz.getMethod(name);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private java.lang.reflect.Method findSetter(Class<?> clazz, String property)
            throws SecurityException, NoSuchMethodException {
        String name = "set" + Character.toUpperCase(property.charAt(0)) + property.substring(1);
        for (java.lang.reflect.Method method : clazz.getDeclaredMethods()) {
            if (method.getParameterTypes().length == 1 && method.getName().equals(name)) {
                return method;
            }
        }
        throw new NoSuchMethodException(clazz + "." + name + "(<any type>)");
    }

    /**
     * {@inheritDoc}
     */
    public Getter buildParameterGetter(java.lang.reflect.Type[] types, String path) {
        try {
            AccessorKey key = new AccessorKey(types, path);
            Getter getter = getters.get(key);
            if (getter == null) {
                getter = buildGetterClass(null, path, types);
                getters.put(key, getter);
            }
            return getter;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("!", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Setter buildSetter(Class<?> beanClass, String path) {
        try {
            AccessorKey key = new AccessorKey(beanClass, path);
            Setter setter = setters.get(key);
            if (setter == null) {
                setter = buildSetterClass(beanClass, path);
                setters.put(key, setter);
            }
            return setter;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("!", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public java.lang.reflect.Type getParameterType(java.lang.reflect.Method method, String path) {
        return getParameterType(method.getGenericParameterTypes(), path);
    }

    /**
     * {@inheritDoc}
     */
    public java.lang.reflect.Type getParameterType(java.lang.reflect.Type[] types, String path) {
        int pos = path.indexOf('.');
        if (pos == -1) {
            pos = path.length();
        }
        String context = path.substring(0, pos);

        int parameter;
        if (context.equals("value")) {
            parameter = 0;
        } else {
            parameter = Integer.parseInt(context) - 1;
        }

        if (pos == path.length()) {
            return types[parameter];
        }
        String subpath = path.substring(pos + 1);
        return getPropertyType((Class<?>) types[parameter], subpath);
    }

    /**
     * {@inheritDoc}
     */
    public java.lang.reflect.Type getPropertyType(Class<?> beanClass, String property) {
        String[] paths = property.split("\\.");

        java.lang.reflect.Type type = beanClass;
        for (int i = 0; i < paths.length; ++i) {
            // TODO: Cast!
            java.lang.reflect.Method getter = findGetter((Class<?>) type, paths[i]);
            type = getter.getGenericReturnType();
        }
        return type;
    }

    private class ASMClassLoader extends ClassLoader {
        /**
         * 
         */
        public ASMClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }
}
