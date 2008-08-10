package com.google.code.nanorm.test.guide;

import com.google.code.nanorm.annotations.Insert;
import com.google.code.nanorm.annotations.ResultMap;
import com.google.code.nanorm.annotations.ResultMapRef;
import com.google.code.nanorm.annotations.Select;
import com.google.code.nanorm.annotations.SelectKey;
import com.google.code.nanorm.annotations.SelectKeyType;
import com.google.code.nanorm.annotations.Update;

@ResultMap(id = "book", auto = true)
public interface BookMapper {
    @Insert("INSERT INTO books(id, name, author, published) " +
      "VALUES(${1.id}, ${1.name}, ${1.author}, ${1.published})")
    void insertBook(Book book);
    
    @ResultMapRef("book")
    @Select("SELECT id, name, author, published FROM books WHERE id = ${1}")
    Book selectBook(int id);
    
    @Update("UPDATE books SET name = ${1.name}, author = ${1.author}, " + 
      "published = ${1.published} WHERE id = ${1.id}")
    int updateBook(Book book);
    
    @Update("DELETE FROM books WHERE id = ${1}")
    int deleteBook(int id);
    
    @Insert("INSERT INTO books(id, name, author, published) " +
    "VALUES(NEXT VALUE FOR SEQ, ${1.name}, ${1.author}, ${1.published})")
    @SelectKey
    int insertBook2(Book book);
    
    @Insert("INSERT INTO books(id, name, author, published) " +
    "VALUES(NEXT VALUE FOR SEQ, ${1.name}, ${1.author}, ${1.published})")
    @SelectKey("SELECT CURRVAL('SEQ')")
    int insertBook3(Book book);
}