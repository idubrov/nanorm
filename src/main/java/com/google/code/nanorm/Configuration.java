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

package com.google.code.nanorm;

import com.google.code.nanorm.internal.FactoryImpl;
import com.google.code.nanorm.internal.config.InternalConfiguration;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;
import com.google.code.nanorm.internal.introspect.asm.ASMIntrospectionFactory;
import com.google.code.nanorm.internal.introspect.reflect.ReflectIntrospectionFactory;
import com.google.code.nanorm.internal.type.TypeHandlerFactoryImpl;

/**
 * Public configuration class for nanorm factory creation.
 * @author Ivan Dubrov
 * @version 1.0 19.06.2008
 */
public class Configuration {

    private TypeHandlerFactory typeHandlerFactory;

    private IntrospectionFactory introspectionFactory;

    /**
     * 
     */
    public Configuration() {
        typeHandlerFactory = new TypeHandlerFactoryImpl();
        //introspectionFactory = new BeanUtilsIntrospectionFactory();
        
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        introspectionFactory = new ASMIntrospectionFactory(cl);
    }

    public Factory buildFactory() {
        InternalConfiguration config = new InternalConfiguration(typeHandlerFactory,
                introspectionFactory);
        return new FactoryImpl(config);
    }  
}
