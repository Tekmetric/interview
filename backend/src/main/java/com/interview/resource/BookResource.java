package com.interview.resource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;


@RestController
public class BookResource {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @GetMapping("/api/books")
    public Response fetchAll() {   
        List<Book> bookList = new ArrayList<>(); 
        try {
          bookList = fetchAllBooks();
        } catch (SQLException e){
          return new Response(e.toString(), 501);
        }
        return new BookListResponse(bookList, "", 200);
    }

    private List<Book> fetchAllBooks() throws SQLException{
        return jdbcTemplate.query("SELECT * FROM BOOKS;",
        (rs, rowNum) -> new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getInt("publishYear")));
    }

    @GetMapping("/api/book")
    public Response fetch(@RequestParam(value = "id", defaultValue = "") String id) {
        try {
            int i = Integer.parseInt(id);
        } catch (NumberFormatException e){
            return new BookResponse("Invalid parameter id, must be a string", 422);
        }

        List<Book> books = new ArrayList<>();
        try {
            books = fetchBook(id);
        } catch (SQLException e) {
            return new Response(e.toString(), 501);
        }

        if (books.size() > 0) {
            return new BookResponse(books.get(0), "", 200);
        }
        return new BookResponse("Error: no entities found with id: " + id, 404);
    }

    private List<Book> fetchBook(String id) throws SQLException {
        List<Book> bookList= jdbcTemplate.query("SELECT * FROM BOOKS WHERE ID=" + id,
          (rs, rowNum) -> new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getInt("publishYear")));
        return bookList;
    }

    @PostMapping("/api/book/create")
    public Response create(@RequestBody Book book) {
        Number idKey = null;
        try {
            idKey = createBook(book);
        } catch (SQLException e) {
            return new Response(e.toString(), 501);
        }

        if (idKey != null) {
            int generatedId = idKey.intValue();
            List<Book> books = new ArrayList<>();
            try {
                books = fetchBook(String.valueOf(generatedId));
            } catch (SQLException e) {
                return new Response(e.toString(), 501);
            }
            return new BookResponse(books.get(0), "", 200);
            
        } 
        return new BookResponse("Error creating book", 501);
    }

    private Number createBook(Book book) throws SQLException {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        String sqlStatement = "INSERT INTO Books(Title, Author, PublishYear) VALUES(:title, :author, :publishYear);";
        Map<String, Object> params = new HashMap<>();

        params.put("title", book.getTitle());
        params.put("author", book.getAuthor());
        params.put("publishYear", book.getPublishYear());
        
        namedParameterJdbcTemplate.update(sqlStatement, new MapSqlParameterSource(params), generatedKeyHolder);
        return generatedKeyHolder.getKey();
    }

    @DeleteMapping("/api/book/delete")
    public Response delete(@RequestParam(value = "id", defaultValue = "") String id) {
        try {
            deleteBook(id);
        } catch (SQLException e) {
            return new Response(e.toString(), 501);
        }

        return new Response("", 200);
    }

    private void deleteBook(String id) throws SQLException {
        String sqlStatement = "DELETE FROM Books where id =:id;";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        namedParameterJdbcTemplate.update(sqlStatement, params);
    }

    @PutMapping("/api/book/update")
    public Response update(@RequestBody Book book) {
        Number idKey = null;
        try {
            idKey = updateBook(book);
        } catch (SQLException e) {
            return new Response(e.toString(), 501);
        }

        if (idKey != null) {
            int generatedId = idKey.intValue();
            List<Book> books = new ArrayList<>();
            try {
                books = fetchBook(String.valueOf(generatedId));
            } catch (SQLException e) {
                return new Response(e.toString(), 501);
            }
            return new BookResponse(books.get(0), "", 200);
        } 
        return new BookResponse("Error updating book", 501);
    }

    private Number updateBook(Book book) throws SQLException {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        String sqlStatement = "UPDATE Books SET Title=:title, Author=:author, PublishYear=:publishYear WHERE id=:id;";

        Map<String, Object> params = new HashMap<>();
        params.put("id", book.getId());
        params.put("title", book.getTitle());
        params.put("author", book.getAuthor());
        params.put("publishYear", book.getPublishYear());
        
        namedParameterJdbcTemplate.update(sqlStatement, new MapSqlParameterSource(params), generatedKeyHolder);
        return generatedKeyHolder.getKey();
    }
}
