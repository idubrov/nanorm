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
package com.google.code.nanorm.internal.config;

import java.lang.reflect.Type;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.code.nanorm.internal.Fragment;
import com.google.code.nanorm.internal.mapping.result.ResultMap;

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 28.05.2008
 */
public class StatementConfig {
    
    public static final int RETURN_VALUE = -1;
    
    private final String id;
    
    private boolean update;
    
    private Fragment statementBuilder;
    
    private ResultMap resultMapper;
    
    private Type resultType;
    
    private Type[] parameterTypes;
    
    private int callbackIndex = -1;
    
    /**
     * 
     */
    public StatementConfig(String id) {
        this.id = id;
    }
    
    /** @return Returns the update. */
    public boolean isUpdate() {
        return update;
    }
    
    /** @param update The update to set. */
    public void setUpdate(boolean update) {
        this.update = update;
    }

    /** @return Returns the statementBuilder. */
    public Fragment getStatementBuilder() {
        return statementBuilder;
    }

    /** @param statementBuilder The statementBuilder to set. */
    public void setStatementBuilder(Fragment statementBuilder) {
        this.statementBuilder = statementBuilder;
    }

    /** @return Returns the resultMapper. */
    public ResultMap getResultMapper() {
        return resultMapper;
    }

    /** @param resultMapper The resultMapper to set. */
    public void setResultMapper(ResultMap resultMapper) {
        this.resultMapper = resultMapper;
    }

    /** @return Returns the resultType. */
    public Type getResultType() {
        return resultType;
    }

    /** @param resultType The resultType to set. */
    public void setResultType(Type resultType) {
        this.resultType = resultType;
    }
    
    /** @return Returns the parameterTypes. */
    public Type[] getParameterTypes() {
        return parameterTypes;
    }
    
    /** @param parameterTypes The parameterTypes to set. */
    public void setParameterTypes(Type[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
    
    /** @return Returns the id. */
    public String getId() {
        return id;
    }
    
    /** @return Returns the callbackIndex. */
    public int getCallbackIndex() {
        return callbackIndex;
    }
    
    /** @param callbackIndex The callbackIndex to set. */
    public void setCallbackIndex(int callbackIndex) {
        this.callbackIndex = callbackIndex;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).
            append("id", id).
            append("update", update).
            append("resultType", resultType).
            append("resultMapper", resultMapper).
            append("statementBuilder", statementBuilder).
            append("parameterTypes", parameterTypes).
            toString();
    }
}
