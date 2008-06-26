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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.code.nanorm.internal.BoundFragment;
import com.google.code.nanorm.internal.TextFragment;
import com.google.code.nanorm.internal.introspect.IntrospectionFactory;

public class SQLSource implements BoundFragment {

	/**
	 * Stack of list of bound fragments. Each basic "append" operation adds
	 * bound fragment to the list on stack top.
	 */
	private List<List<BoundFragment>> stack = new ArrayList<List<BoundFragment>>();

	private IntrospectionFactory introspectionFactory;

	public static class Join implements BoundFragment {
		private String open;

		private String close;

		private String with;

		private List<List<BoundFragment>> clauses = new ArrayList<List<BoundFragment>>();

		public Join open(String open) {
			this.open = open;
			return this;
		}

		public Join close(String close) {
			this.close = close;
			return this;
		}

		public Join with(String with) {
			this.with = with;
			return this;
		}

		public void generate(StringBuilder builder, List<Object> parameters,
				List<Type> types) {
			if (open != null) {
				builder.append(open);
			}
			boolean flag = false;
			// Join elements in the clauses list
			for (List<BoundFragment> items : clauses) {
				if (items.size() > 0 && flag && with != null) {
					builder.append(with);
				}
				for (BoundFragment item : items) {
					item.generate(builder, parameters, types);
				}
				if(items.size() > 0) {
					flag = true;
				}
			}

			if (close != null) {
				builder.append(close);
			}
		}

	}

	/**
	 * Get last bound fragment on the stack.
	 * 
	 * @return last bound fragment
	 */
	private List<BoundFragment> last() {
		return stack.get(stack.size() - 1);
	}

	/**
	 * Constructor.
	 */
	public SQLSource() {
		stack.add(new ArrayList<BoundFragment>());
	}

	/** @param introspectionFactory The introspectionFactory to set. */
	public void setIntrospectionFactory(
			IntrospectionFactory introspectionFactory) {
		this.introspectionFactory = introspectionFactory;
	}

	/**
	 * Append given SQL fragment with parameters to the current dynamic
	 * statement.
	 * 
	 * @param fragment SQL fragment
	 * @param params clause parameters
	 */
	public void append(String fragment, Object... params) {
		BoundFragment f = new TextFragment(fragment, introspectionFactory)
				.bindParameters(params);
		last().add(f);
	}

	/**
	 * Iterate the parameters and apply block to each of them.
	 * @param <T> parameters type
	 * @param block block to apply to each parameter
	 * @param params parameters
	 * @return join object which could be used to configure join parameters.
	 */
	public <T> Join join(ParamBlock<T> block, T... params) {
		Join join = new Join();

		for (T param : params) {
			List<BoundFragment> items = new ArrayList<BoundFragment>();
			join.clauses.add(items);
			stack.add(items);
			try {
				block.generate(param);
			} finally {
				assert last() == items;
				stack.remove(stack.size() - 1);
			}
		}
		last().add(join);
		return join;
	}
	
	
	public Join join(Block... blocks) {
		Join join = new Join();

		for (Block block : blocks) {
			List<BoundFragment> items = new ArrayList<BoundFragment>();
			join.clauses.add(items);
			stack.add(items);
			try {
				block.generate();
			} finally {
				assert last() == items;
				stack.remove(stack.size() - 1);
			}
		}
		last().add(join);
		return join;
	}

	/**
	 * Generate the final SQL fragemnt with parameter and types.
	 * 
	 * @param builder SQL fragment builder
	 * @param parameters parameters
	 * @param types parameter types
	 */
	public void generate(StringBuilder builder, List<Object> parameters,
			List<Type> types) {
		assert (stack.size() == 1);
		for (BoundFragment obj : last()) {
			obj.generate(builder, parameters, types);
		}
	}

	// TODO: toString
}
