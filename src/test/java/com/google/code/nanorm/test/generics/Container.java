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

package com.google.code.nanorm.test.generics;

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 20.06.2008
 */
@SuppressWarnings("all")
public class Container {
    
    private Wrapper<Wrapper<Thing>> item;
    
    private Wrapper2<Thing> item2;
    
    private Wrapper<? extends Thing> item3;
    
    private Wrapper3<Thing> item5;
    
    private Wrapper<Thing[]> item6;

    public Wrapper<Wrapper<Thing>> getItem() {
        return item;
    }

    public void setItem(Wrapper<Wrapper<Thing>> item) {
        this.item = item;
    }
    
    public Wrapper2<Thing> getItem2() {
        return item2;
    }

    public void setItem2(Wrapper2<Thing> item2) {
        this.item2 = item2;
    }

    public Wrapper<? extends Thing> getItem3() {
        return item3;
    }

    public void setItem3(Wrapper<? extends Thing> item3) {
        this.item3 = item3;
    }

    public Wrapper3<Thing> getItem5() {
        return item5;
    }

    public void setItem5(Wrapper3<Thing> item5) {
        this.item5 = item5;
    }
    
    public Wrapper<Thing[]> getItem6() {
        return item6;
    }

    public void setItem6(Wrapper<Thing[]> item6) {
        this.item6 = item6;
    }
}
