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
package com.google.code.nanorm.test.resultmap;

import java.util.List;

import com.google.code.nanorm.SQLSource;
import com.google.code.nanorm.annotations.Mapping;
import com.google.code.nanorm.annotations.ResultMap;
import com.google.code.nanorm.annotations.ResultMapList;
import com.google.code.nanorm.annotations.ResultMapRef;
import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.annotations.Source;
import com.google.code.nanorm.test.beans.Car;

/**
 *
 * @author Ivan Dubrov
 * @version 1.0 27.05.2008
 */
@ResultMapList({
    @ResultMap(id = "car", auto = true, mappings = {     
        @Mapping(property = "owner.firstName", column = "owner")
    })
})
public interface CarMapper {
    @ResultMapRef("car")
    @Select("SELECT id, model, owner, year FROM cars WHERE ID = ${1}")
    Car getCarById(int id);
    
    @ResultMapRef("car")
    @Select("SELECT id, model, owner, year FROM cars ORDER BY id ASC")
    List<Car> listCars();
    
    @Source(SelectModelYearSource.class)
    @ResultMapRef("car")
    List<Car> listByModelYear(String model, int year);
    
    public static class SelectModelYearSource extends SQLSource {
        public void sql(String model, int year)
        {
            append("SELECT id, model, owner, year FROM cars WHERE ");
            if(model != null) {
                append(" model = ${value}", model);
                if(year != 0) {
                    append(" AND ");
                }
            }
            if(year != 0) {
                append(" YEAR = ${value}", year);
            }
            append(" ORDER BY id ASC");
        }
    }
}
