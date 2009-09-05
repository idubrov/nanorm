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

import java.util.List;

import com.google.code.nanorm.DataSink;
import com.google.code.nanorm.annotations.Insert;
import com.google.code.nanorm.annotations.ResultMap;
import com.google.code.nanorm.annotations.ResultMapRef;
import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.annotations.SelectKey;
import com.google.code.nanorm.annotations.SelectKeyType;
import com.google.code.nanorm.annotations.Update;

@SuppressWarnings("all")
@ResultMap(id = "book", auto = true)
public interface BookMapper {
    @Insert("INSERT INTO books(id, name, author, published) "
            + "VALUES(${1.id}, ${1.name}, ${1.author}, ${1.published})")
    void insertBook(Book book);

    @ResultMapRef("book")
    @Select("SELECT id, name, author, published FROM books WHERE id = ${1}")
    Book selectBook(int id);

    @Update("UPDATE books SET name = ${1.name}, author = ${1.author}, "
            + "published = ${1.published} WHERE id = ${1.id}")
    int updateBook(Book book);

    @Update("DELETE FROM books WHERE id = ${1}")
    int deleteBook(int id);

    @Insert("INSERT INTO books(id, name, author, published) "
            + "VALUES(NEXT VALUE FOR SEQ, ${1.name}, ${1.author}, ${1.published})")
    @SelectKey
    int insertBook2(Book book);

    @Insert("INSERT INTO books(id, name, author, published) "
            + "VALUES(NEXT VALUE FOR SEQ, ${1.name}, ${1.author}, ${1.published})")
    @SelectKey("SELECT CURRVAL('SEQ')")
    int insertBook3(Book book);

    @Insert("INSERT INTO books(id, name, author, published) "
            + "VALUES(NEXT VALUE FOR SEQ, ${1.name}, ${1.author}, ${1.published})")
    @SelectKey(property = "1.id")
    void insertBook4(Book book);

    @Insert("INSERT INTO books(id, name, author, published) "
            + "VALUES(${1.id}, ${1.name}, ${1.author}, ${1.published})")
    @SelectKey(value = "SELECT NEXTVAL('SEQ')", property = "1.id", type = SelectKeyType.BEFORE)
    void insertBook5(Book book);

    @ResultMapRef("book")
    @Select("SELECT id, name, author, published FROM books")
    Book[] listBooks();

    @ResultMapRef("book")
    @Select("SELECT id, name, author, published FROM books")
    List<Book> listBooks2();

    @ResultMapRef("book")
    @Select("SELECT id, name, author, published FROM books")
    void listBooks3(DataSink<Book> sink);
}