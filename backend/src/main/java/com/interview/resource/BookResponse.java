package com.interview.resource;

public class BookResponse extends Response {
  private Book book;

  public BookResponse(Book book, String error, int responseCode) {
    super(error, responseCode);
    this.book = book;
  }

    public BookResponse(String error, int responseCode) {
      super(error, responseCode);
    }

    public Book getBook() {
      return this.book;
    }
}
