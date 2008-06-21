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

import java.util.List;

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
public class Owner {
    
    private int id;
    
    private String firstName;
    
    private String lastName;
    
    private List<Crash> crashes;
    
    private Crash[] crashes2;
    
    private int[] crashes3;
    
    /** @return Returns the id. */
    public int getId() {
        return id;
    }

    /** @param id The id to set. */
    public void setId(int id) {
        this.id = id;
    }

    /** @return Returns the name. */
    public String getFirstName() {
        return firstName;
    }

    /** @param name The name to set. */
    public void setFirstName(String name) {
        this.firstName = name;
    }

    /** @return Returns the lastName. */
    public String getLastName() {
        return lastName;
    }

    /** @param lastName The lastName to set. */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /** @return Returns the crashes. */
    public List<Crash> getCrashes() {
        return crashes;
    }

    /** @param crashes The crashes to set. */
    public void setCrashes(List<Crash> crashes) {
        this.crashes = crashes;
    }
    
    /** @return Returns the crashes2. */
    public Crash[] getCrashes2() {
        return crashes2;
    }
    
    /** @param crashes2 The crashes2 to set. */
    public void setCrashes2(Crash[] crashes2) {
        this.crashes2 = crashes2;
    }

    /** @return Returns the crashes3. */
    public int[] getCrashes3() {
        return crashes3;
    }
    
    /** @param crashes3 The crashes3 to set. */
    public void setCrashes3(int[] crashes3) {
        this.crashes3 = crashes3;
    }
}
