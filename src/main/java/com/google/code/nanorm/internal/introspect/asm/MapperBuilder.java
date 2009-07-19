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

import static com.google.code.nanorm.internal.introspect.asm.Constants.CTOR;
import static com.google.code.nanorm.internal.introspect.asm.Constants.MAPPER_CTOR;
import static com.google.code.nanorm.internal.introspect.asm.Constants.OBJECT_TYPE;
import static com.google.code.nanorm.internal.introspect.asm.Constants.QUERY_DELEGATE_TYPE;
import static com.google.code.nanorm.internal.introspect.asm.Constants.QUERY_METHOD;
import static com.google.code.nanorm.internal.introspect.asm.Constants.STATEMENT_CONFIGS_ARR_TYPE;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.google.code.nanorm.internal.config.StatementConfig;

/**
 * ASM-based mapper implementation builder.
 * 
 * @author Ivan Dubrov
 * @version 1.0 22.06.2008
 */
public class MapperBuilder {

    /**
     * Build mapper.
     * 
     * @param name class name
     * @param mapper mapper interface or base class
     * @param configs method configurations
     * @return mapper byte-code
     */
    public static byte[] buildMapper(String name, Class<?> mapper, MethodConfig[] configs) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        Type owner = Type.getType('L' + name + ';');

        if (mapper.isInterface()) {
            cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, name, null, "java/lang/Object",
                    new String[] {mapper.getName().replace('.', '/') });
        } else {
            cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, name, null, mapper.getName().replace('.', '/'),
                    null);
        }

        cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, "delegate", QUERY_DELEGATE_TYPE
                .getDescriptor(), null, null);

        cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, "configs",
                STATEMENT_CONFIGS_ARR_TYPE.getDescriptor(), null, null);

        if (mapper.isInterface()) {
            visitConstructor(cw, owner, OBJECT_TYPE);
        }
        else {
            visitConstructor(cw, owner, Type.getType(mapper));
        }

        for (MethodConfig cfg : configs) {
            visitMethod(owner, cw, cfg);
        }

        cw.visitEnd();

        return cw.toByteArray();
    }

    /**
     * Generate constructor.
     * 
     * @param cw class writer
     * @param owner self type
     * @param baseType
     */
    private static void visitConstructor(ClassWriter cw, Type owner, Type baseType) {
        GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, MAPPER_CTOR, null, null,
                cw);
        mg.loadThis();
        mg.invokeConstructor(baseType, CTOR);
        mg.loadThis();
        mg.loadArg(0);
        mg.putField(owner, "delegate", QUERY_DELEGATE_TYPE);
        mg.loadThis();
        mg.loadArg(1);
        mg.putField(owner, "configs", STATEMENT_CONFIGS_ARR_TYPE);
        mg.returnValue();
        mg.endMethod();
    }

    /**
     * Generate mapper method.
     * 
     * @param owner self type
     * @param cw class writer
     * @param config method configuration
     */
    private static void visitMethod(Type owner, ClassWriter cw, MethodConfig config) {
        java.lang.reflect.Method ifaceMethod = config.getMethod();
        Type returnType = Type.getType(ifaceMethod.getReturnType());
        Type[] args = new Type[ifaceMethod.getParameterTypes().length];
        for (int i = 0; i < args.length; ++i) {
            args[i] = Type.getType(ifaceMethod.getParameterTypes()[i]);
        }

        Method method = new Method(ifaceMethod.getName(), returnType, args);

        GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, method, null, null, cw);

        // Factory
        mg.loadThis();
        mg.getField(owner, "delegate", QUERY_DELEGATE_TYPE);

        // Statement config
        mg.loadThis();
        mg.getField(owner, "configs", STATEMENT_CONFIGS_ARR_TYPE);
        mg.push(config.getIndex());
        mg.arrayLoad(Type.getType(StatementConfig.class));

        // Arguments
        mg.loadArgArray();

        mg.invokeInterface(QUERY_DELEGATE_TYPE, QUERY_METHOD);
        mg.unbox(returnType);
        mg.returnValue();
        mg.endMethod();
    }
}
