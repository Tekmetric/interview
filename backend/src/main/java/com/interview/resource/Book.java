package com.interview.resource;

public class Book {
    private int id, publishYear;
    private String title, author;

    public Book() {
    }

    public Book(int id, String title, String author, int publishYear) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publishYear = publishYear;
    }

    public Book(String title, String author, int publishYear) {
        this.title = title;
        this.author = author;
        this.publishYear = publishYear;
    }

    public Book(int id) {
      this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getAuthor() {
        return this.author;
    }

    public int getPublishYear() {
        return this.publishYear;
    }
}
