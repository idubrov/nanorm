/**
 * Copyright (C) 2008, 2009 Ivan S. Dubrov
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

package com.google.code.nanorm.test.guide;

import java.sql.Date;

/**
 * Sample bean for guide.
 * @author Ivan Dubrov
 */
public class Book {
    private int id;

    private String name;

    private String author;

    private Date published;

    /**
     * Getter for id.
     * @return id.
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for id.
     * @param id id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for name.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name.
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for author.
     * @return author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Setter for author.
     * @param author author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Getter for published.
     * @return published
     */
    public Date getPublished() {
        return published;
    }

    /**
     * Setter for published.
     * @param published published
     */
    public void setPublished(Date published) {
        this.published = published;
    }
}