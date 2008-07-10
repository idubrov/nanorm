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

import com.google.code.nanorm.ResultCallback;
import com.google.code.nanorm.internal.Fragment;
import com.google.code.nanorm.internal.introspect.Setter;
import com.google.code.nanorm.internal.mapping.result.RowMapper;

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

	private final String id;

	private boolean update;

	private boolean insert;

	private Fragment fragment;

	private RowMapper resultMapper;

	private Type resultType;

	private Type[] parameterTypes;

	private int callbackIndex = RETURN_VALUE;

	private StatementConfig selectKey;

	private boolean selectKeyAfter;
	
	private Setter keySetter;

	/**
	 * Constructor.
	 * 
	 * @param id id
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

	/** @return the insert */
	public boolean isInsert() {
		return insert;
	}

	/** @param insert the insert to set */
	public void setInsert(boolean insert) {
		this.insert = insert;
	}

	/** @return Returns the statementBuilder. */
	public Fragment getStatementBuilder() {
		return fragment;
	}

	/** @param statementBuilder The statementBuilder to set. */
	public void setStatementBuilder(Fragment statementBuilder) {
		this.fragment = statementBuilder;
	}

	/** @return Returns the resultMapper. */
	public RowMapper getResultMapper() {
		return resultMapper;
	}

	/** @param resultMapper The resultMapper to set. */
	public void setResultMapper(RowMapper resultMapper) {
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

	/** @return the selectKey */
	public StatementConfig getSelectKey() {
		return selectKey;
	}

	/** @param selectKey the selectKey to set */
	public void setSelectKey(StatementConfig selectKey) {
		this.selectKey = selectKey;
	}
	
	/** @return the selectKeyAfter */
	public boolean isSelectKeyAfter() {
		return selectKeyAfter;
	}

	/** @param selectKeyAfter the selectKeyAfter to set */
	public void setSelectKeyAfter(boolean selectKeyAfter) {
		this.selectKeyAfter = selectKeyAfter;
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
	public String getId() {
		return id;
	}

	/**
	 * Get index of the parameter that is instance of {@link ResultCallback},
	 * which will be used for processing the results.
	 * 
	 * @see #RETURN_VALUE
	 * @return callback parameter index.
	 */
	public int getCallbackIndex() {
		return callbackIndex;
	}

	/**
	 * Set index of the parameter that is instance of {@link ResultCallback},
	 * which will be used for processing the results.
	 * 
	 * @see #RETURN_VALUE
	 * @param callbackIndex callback parameter index.
	 */
	public void setCallbackIndex(int callbackIndex) {
		this.callbackIndex = callbackIndex;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("update",
				update).append("resultType", resultType).append("resultMapper",
				resultMapper).append("fragment", fragment)
				.append("parameterTypes", parameterTypes).append("selectKey",
						selectKey).append("selectKeyAfter", selectKeyAfter)
				.toString();
	}
}
