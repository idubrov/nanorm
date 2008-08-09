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
import static com.google.code.nanorm.internal.introspect.asm.Constants.OBJECT_TYPE;
import static com.google.code.nanorm.internal.introspect.asm.Constants.SET_VALUE;
import static com.google.code.nanorm.internal.introspect.asm.Constants.STRING_TYPE;
import static com.google.code.nanorm.internal.introspect.asm.Constants.SUBSTRING;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.google.code.nanorm.internal.introspect.IntrospectUtils;
import com.google.code.nanorm.internal.introspect.PropertyVisitor;

/**
 * Code generator for properties access. Can generate both getters and setters.
 * Code generation is implemented with ASM library. The result is array of bytes
 * with Java class definition.
 * 
 * TODO: Support for classcast exception (exception should contain position in the property path)!
 * 
 * @author Ivan Dubrov
 * @version 1.0 22.06.2008
 */
public final class AccessorBuilder implements PropertyVisitor<byte[]> {

	private final ClassWriter cw;

	private String fullPath;

	private Class<?> initialBeanClass;

	/**
	 * Declared type on top of the stack.
	 */
	private Class<?> declaredClass;

	private GeneratorAdapter accessormg;

	private final Label npeLabel = new Label();

	private int npeLocal = -1;

	private final boolean isSetter;

	/**
	 * Constructor. Starts generating the Java class code.
	 * 
	 * @param name accessor class name
	 * @param isSetter if we building accessor for setter.
	 */
	public AccessorBuilder(String name, boolean isSetter) {
		this.isSetter = isSetter;

		cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		Type owner = Type.getType("L" + name + ";");
		if (isSetter) {
			String iface = "com/google/code/nanorm/internal/introspect/Setter";
			cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, name, null,
					"java/lang/Object", new String[] { iface });

			visitSetterConstructor();
		} else {
			String iface = "com/google/code/nanorm/internal/introspect/Getter";
			cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, name, null,
					"java/lang/Object", new String[] { iface });

			cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, "type",
					"Ljava/lang/reflect/Type;", null, null);

			visitGetterConstructor(owner);
			visitGetType(owner);
		}
	}

	/**
	 * Generate code for setter constructor.
	 */
	private void visitSetterConstructor() {
		Method ctor = Method.getMethod("void <init>()");

		GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, ctor, null,
				null, cw);
		mg.loadThis();
		mg.invokeConstructor(Type.getType(Object.class), ctor);
		mg.returnValue();
		mg.endMethod();
	}

	/**
	 * Generate code for getter constructor.
	 * @param owner type representing the class being generated
	 */
	private void visitGetterConstructor(Type owner) {
		GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC,
				GETTER_CTOR, null, null, cw);

		mg.loadThis();
		mg.invokeConstructor(OBJECT_TYPE, CTOR);
		mg.loadThis();
		mg.loadArg(0);
		mg.putField(owner, "type", JL_REFLECT_TYPE_TYPE);
		mg.returnValue();
		mg.endMethod();
	}

	/**
	 * Generate {@link com.google.code.nanorm.internal.introspect.Getter#getType()} method.
	 * 
	 * @param owner type representing the class being generated
	 */
	private void visitGetType(Type owner) {
		Method getTypeMethod = Method
				.getMethod("java.lang.reflect.Type getType()");

		GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC,
				getTypeMethod, null, null, cw);

		mg.loadThis();
		mg.getField(owner, "type", JL_REFLECT_TYPE_TYPE);
		mg.returnValue();
		mg.endMethod();
	}

	/**
	 * {@inheritDoc}
	 */
	public void visitBegin(Class<?> beanClass, String path) {
		this.fullPath = path;
		this.initialBeanClass = beanClass;

		if (isSetter) {
			// void setValue(Object instance);
			accessormg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, SET_VALUE,
					null, null, cw);
		} else {
			// Object getValue(Object instance);
			accessormg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, GET_VALUE,
					null, null, cw);
		}
		npeLocal = accessormg.newLocal(Type.INT_TYPE);
		accessormg.visitCode();

		// Load argument and remember type on the stack (which is Object),
		// see instance parameter of Getter#getValue/Setter#setValue
		accessormg.loadArg(0);
		declaredClass = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<?> visitIndex(int pos, int index, boolean hasNext,
			Class<?> beanClass) {
		checkNull(pos);

		// Do cast if class on top of the stack does not match the type expected
		if (declaredClass != beanClass) {
			accessormg.checkCast(Type.getType(beanClass));
		}

		if (!hasNext && isSetter) {
			// Push array index
			accessormg.push(index);

			// Cast parameter to required type
			Type type = Type.getType(beanClass.getComponentType());
			accessormg.loadArg(1);
			accessormg.unbox(type);

			// Store to array
			accessormg.arrayStore(type);
		} else {
			// Load from array
			accessormg.push(index);
			Type type = Type.getType(beanClass.getComponentType());
			accessormg.arrayLoad(type);

			// If this is last property in the path, box the result to match
			// Getter#getValue return value
			if (!hasNext) {
				accessormg.box(type);
			}

			// Remember type on top of the stack
			declaredClass = beanClass.getComponentType();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<?> visitProperty(int pos, String property,
			java.lang.reflect.Method getter, boolean hasNext, Class<?> beanClass) {
		checkNull(pos);

		// If expected class is not equal to the type on top of the stack, do
		// the cast
		if (declaredClass != beanClass) {
			accessormg.checkCast(Type.getType(beanClass));
		}

		if (!hasNext && isSetter) {
			java.lang.reflect.Method setter = IntrospectUtils.findSetter(
					beanClass, property);
			Type type = Type.getType(setter.getParameterTypes()[0]);

			// Cast parameter to required type
			accessormg.loadArg(1);
			accessormg.unbox(type);

			Method method = new Method(setter.getName(), Type.VOID_TYPE,
					new Type[] { type });
			accessormg.invokeVirtual(Type.getType(beanClass), method);
		} else {

			Type type = Type.getType(getter.getReturnType());
			Method method = new Method(getter.getName(), type, new Type[0]);

			accessormg.invokeVirtual(Type.getType(beanClass), method);

			// If this is last property in the path, box the result to match
			// Getter#getValue return value
			if (!hasNext) {
				accessormg.box(type);
			}

			// Remember type on top of the stack
			declaredClass = getter.getReturnType();
		}
		return null;
	}

	/**
	 * Generate the code that performs null check
	 * @param pos position in the property path (for better error messages)
	 */
	private void checkNull(int pos) {
		// Check current value is not null
		accessormg.dup();
		
		// Store current path position in local variable for better diagnostics
		accessormg.push(pos); 
		accessormg.storeLocal(npeLocal);
		// diagnostics
		accessormg.ifNull(npeLabel);
	}

	/**
	 * NPE handling code. We have position in the property path in the
	 * {@link #npeLocal} local variable.
	 */
	private void npeHandler() {
		
		accessormg.visitLabel(npeLabel);
		
		// Create the exception
		accessormg.newInstance(NPE_TYPE);
		accessormg.dup();

		// Generate the error message
		accessormg.push(fullPath);
		accessormg.push(0);
		accessormg.loadLocal(npeLocal);
		accessormg.invokeVirtual(STRING_TYPE, SUBSTRING);
		accessormg.push(" property is null for " + initialBeanClass.getName()
				+ " instance (full path is " + fullPath + ").");
		accessormg.invokeVirtual(STRING_TYPE, CONCAT);

		// Now we have ex, ex, msg on stack
		accessormg.invokeConstructor(NPE_TYPE, NPE_CTOR);
		accessormg.throwException();
	}

	/**
	 * {@inheritDoc}
	 */
	public byte[] visitEnd() {
		accessormg.returnValue();
		npeHandler();

		accessormg.endMethod();
		cw.visitEnd();

		byte[] code = cw.toByteArray();
		return code;
	}
}
