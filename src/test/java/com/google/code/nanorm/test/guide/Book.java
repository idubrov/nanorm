package com.google.code.nanorm.test.guide;

import java.sql.Date;

public class Book {
    private int id;
    
    private String name;
    
    private String author;
    
    private Date published;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public Date getPublished() {
		return published;
    }
    
    public void setPublished(Date published) {
		this.published = published;
	}
}