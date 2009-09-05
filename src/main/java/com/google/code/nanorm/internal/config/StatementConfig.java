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
import java.sql.ResultSet;

import javax.swing.text.html.Option;

import com.google.code.nanorm.annotations.FetchDirection;
import com.google.code.nanorm.annotations.Options;
import com.google.code.nanorm.annotations.ResultSetConcurrency;
import com.google.code.nanorm.annotations.ResultSetType;
import com.google.code.nanorm.annotations.SelectKeyType;
import com.google.code.nanorm.internal.Fragment;
import com.google.code.nanorm.internal.introspect.Setter;
import com.google.code.nanorm.internal.mapping.result.RowMapper;
import com.google.code.nanorm.internal.util.ToStringBuilder;

/**
 * Statement confniguration.
 * 
 * @author Ivan Dubrov
 * @version 1.0 28.05.2008
 */
public class StatementConfig {

    /**
     * Special value for callback index meaning we have no callback, but should
     * return result instead.
     */
    public static final int RETURN_VALUE = -1;

    private final StatementKey id;

    private QueryKind kind;

    private Fragment fragment;

    private RowMapper rowMapper;

    private Type resultType;

    private Type[] parameterTypes;

    private int callbackIndex = RETURN_VALUE;

    private StatementConfig selectKey;

    private SelectKeyType selectKeyType;

    private Setter keySetter;

    private Options options;

    /**
     * Constructor.
     * 
     * @param id id
     */
    public StatementConfig(StatementKey id) {
        this.id = id;
    }

    /** @return the kind */
    public QueryKind getKind() {
        return kind;
    }

    /** @param kind the kind to set */
    public void setKind(QueryKind kind) {
        this.kind = kind;
    }

    /** @return Returns the statementBuilder. */
    public Fragment getStatementBuilder() {
        return fragment;
    }

    /** @param statementBuilder The statementBuilder to set. */
    public void setStatementBuilder(Fragment statementBuilder) {
        this.fragment = statementBuilder;
    }

    /** @return Returns the rowMapper. */
    public RowMapper getRowMapper() {
        return rowMapper;
    }

    /** @param rowMapper The rowMapper to set. */
    public void setRowMapper(RowMapper rowMapper) {
        this.rowMapper = rowMapper;
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

    /** @return the selectKey */
    public StatementConfig getSelectKey() {
        return selectKey;
    }

    /** @param selectKey the selectKey to set */
    public void setSelectKey(StatementConfig selectKey) {
        this.selectKey = selectKey;
    }

    /** @return the selectKeyType */
    public SelectKeyType getSelectKeyType() {
        return selectKeyType;
    }

    /** @param selectKeyType the selectKeyType to set */
    public void setSelectKeyType(SelectKeyType selectKeyType) {
        this.selectKeyType = selectKeyType;
    }

    /** @return the keySetter */
    public Setter getKeySetter() {
        return keySetter;
    }

    /** @param keySetter the keySetter to set */
    public void setKeySetter(Setter keySetter) {
        this.keySetter = keySetter;
    }

    /** @return Returns the id. */
    public StatementKey getId() {
        return id;
    }

    /**
     * Get index of the parameter that is instance of
     * {@link com.google.code.nanorm.DataSink}, which will be used for
     * processing the results.
     * 
     * @see #RETURN_VALUE
     * @return callback parameter index.
     */
    public int getCallbackIndex() {
        return callbackIndex;
    }

    /**
     * Set index of the parameter that is instance of
     * {@link com.google.code.nanorm.DataSink}, which will be used for
     * processing the results.
     * 
     * @see #RETURN_VALUE
     * @param callbackIndex callback parameter index.
     */
    public void setCallbackIndex(int callbackIndex) {
        this.callbackIndex = callbackIndex;
    }

    /**
     * Get the options.
     * @return options.
     */
    public Options getOptions() {
        return options;
    }

    /**
     * Set the options.
     * @param options options
     */
    public void setOptions(Options options) {
        this.options = options;
    }

    /**
     * Get the result set type.
     * @return result set type.
     */
    public int getResultSetType() {
        int type = ResultSet.TYPE_FORWARD_ONLY;
        if (options != null) {
            if (options.resultSetType() == ResultSetType.TYPE_SCROLL_INSENSITIVE) {
                type = ResultSet.TYPE_SCROLL_INSENSITIVE;
            } else if (options.resultSetType() == ResultSetType.TYPE_SCROLL_SENSITIVE) {
                type = ResultSet.TYPE_SCROLL_SENSITIVE;
            }
        }
        return type;
    }

    /**
     * Get result set concurrency.
     * @return result set concurrency.
     */
    public int getResultSetConcurrency() {
        int concur = ResultSet.CONCUR_READ_ONLY;
        if (options != null && options.concurrency() == ResultSetConcurrency.CONCUR_UPDATABLE) {
            concur = ResultSet.CONCUR_UPDATABLE;
        }
        return concur;
    }
    
    /**
     * Get fetch direction.
     * @return fetch direction
     */
    public int getFetchDirection() {
        int dir = ResultSet.FETCH_FORWARD;
        if(options != null) {
            if (options.direction() == FetchDirection.REVERSE) {
                dir = ResultSet.FETCH_REVERSE;
            } else if (options.direction() == FetchDirection.UNKNOWN) {
                dir = ResultSet.FETCH_UNKNOWN;
            }
        }
        return dir;
    }
    
    /**
     * Get fetch size.
     * @return fetch size.
     */
    public int getFetchSize() {
        return options != null ? options.fetchSize() : 0;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("kind", kind).append(
                "resultType", resultType).append("rowMapper", rowMapper).append("fragment",
                fragment).append("parameterTypes", parameterTypes).append("selectKey", selectKey)
                .append("selectKeyType", selectKeyType).append("options", options).toString();
    }
}
