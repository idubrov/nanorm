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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.code.nanorm.internal.Statement;
import com.google.code.nanorm.internal.TextStatement;

public class SQLSource implements Statement
{
    private List<List<Statement>> stack = new ArrayList<List<Statement>>();
    
    public static class Join implements Statement {
        private String open;
        
        private String close;
        
        private String with;
        
        private List<List<Statement>> clauses = new ArrayList<List<Statement>>();
        
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
        
        public void generate(StringBuilder builder, List<Object> parameters, List<Type> types)
        {
            if(open != null) {
                builder.append(open);
            }
            boolean flag = false;
            for(List<Statement> items : clauses) {
                if(flag && with != null) {
                    builder.append(with);
                }
                flag = true;
                for(Statement item : items) {
                    item.generate(builder, parameters, types);
                }
            }
            
            if(close != null) {
                builder.append(close);
            }
        }
        
    }
    
    protected final List<Statement> last() {
        return stack.get(stack.size() - 1);   
    }
    
    public SQLSource()
    {
        stack.add(new ArrayList<Statement>());
    }
    
    public void append(String clause, Object... params) {
        last().add(new TextStatement(clause).bindParameters((Object[]) params));
    }
    
    public Join join(final String clause, Object... params) {
        return join(new Block<Object>() {
            public void generate(Object parameter)
            {
                append(clause);
            }
        }, params);
    }
    
    public <T> Join join(Block<T> block, T... params) {
        Join join = new Join();
        
        for(T param : params) {
            List<Statement> items = new ArrayList<Statement>();
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

    public void generate(StringBuilder builder, List<Object> parameters, List<Type> types)
    {
        assert(stack.size() == 1);
        for(Statement obj : last()) {
            obj.generate(builder, parameters, types);
        }
    }
    
    // TODO: toString
}
