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
package com.google.code.nanorm.test.beans;

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
public class Car {

    private int id;
    
    private String model;
    
    private Owner owner = new Owner();
    
    private int year;

    /** @return Returns the id. */
    public int getId() {
        return id;
    }

    /** @param id The id to set. */
    public void setId(int id) {
        this.id = id;
    }

    /** @return Returns the model. */
    public String getModel() {
        return model;
    }

    /** @param model The model to set. */
    public void setModel(String model) {
        this.model = model;
    }

    /** @return Returns the owner. */
    public Owner getOwner() {
        return owner;
    }

    /** @param owner The owner to set. */
    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    /** @return Returns the year. */
    public int getYear() {
        return year;
    }

    /** @param year The year to set. */
    public void setYear(int year) {
        this.year = year;
    }
}
