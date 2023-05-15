package com.interview.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.BookDto;
import com.interview.dto.IdWrapperDto;
import com.interview.dto.PageRequestDto;
import com.interview.dto.PageResponseDto;
import com.interview.exception.DuplicateResourceException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookResourceTest {

    private static final String DUPLICATE_RESOURCE_MESSAGE = "Book with title: 'Book1' and author: 'Author1' already exists.";
    private static final String RESOURCE_NOT_FOUND_MESSAGE = "Cannot find book with id: 1";
    @MockBean
    private BookService bookService;
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetBook() throws Exception {
        Long bookId = 1L;
        BookDto bookDto = getBookDto();
        when(bookService.getBook(bookId)).thenReturn(bookDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/books/" + bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookDto.getId().intValue())));

        verify(bookService).getBook(bookId);
    }

    @Test
    public void testGetBook_ResourceNotFoundException() throws Exception {
        Long bookId = 1L;
        when(bookService.getBook(bookId)).thenThrow(new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));

        mockMvc.perform(MockMvcRequestBuilders.get("/books/" + bookId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(RESOURCE_NOT_FOUND_MESSAGE)));

        verify(bookService).getBook(bookId);
    }


    @Test
    public void testGetBooks() throws Exception {
        PageRequestDto pageRequestDto = PageRequestDto.builder()
                .page(0)
                .pageSize(2)
                .build();
        PageResponseDto<BookDto> pageResponseDto = getPageResponseDto(getBookDtos());
        when(bookService.getBooks(pageRequestDto)).thenReturn(pageResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("pageRequestDto", pageRequestDto))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is("Book1")))
                .andExpect(jsonPath("$.content[1].title", is("Book2")));

        verify(bookService).getBooks(pageRequestDto);
    }

    @Test
    public void testCreateBook() throws Exception {
        Long bookId = 1L;
        BookDto bookDto = getBookDto();
        IdWrapperDto idWrapperDto = new IdWrapperDto(bookId);
        when(bookService.createBooks(any())).thenReturn(idWrapperDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookId.intValue())));

        verify(bookService).createBooks(any());
    }

    @Test
    public void testCreateBook_DuplicateResourceException() throws Exception {
        BookDto bookDto = getBookDto();
        when(bookService.createBooks(any())).thenThrow(new DuplicateResourceException(DUPLICATE_RESOURCE_MESSAGE));

        mockMvc.perform(MockMvcRequestBuilders.post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is(DUPLICATE_RESOURCE_MESSAGE)));

        verify(bookService).createBooks(any());
    }

    @Test
    public void testUpdateBook() throws Exception {
        BookDto bookDto = getBookDto();

        mockMvc.perform(MockMvcRequestBuilders.patch("/books/" + 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isOk());

        verify(bookService).updateBook(bookDto);
    }

    @Test
    public void testUpdateBook_DuplicateResourceException() throws Exception {
        BookDto bookDto = getBookDto();
        doThrow(new DuplicateResourceException(DUPLICATE_RESOURCE_MESSAGE)).when(bookService).updateBook(bookDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/books/" + 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is(DUPLICATE_RESOURCE_MESSAGE)));

        verify(bookService).updateBook(bookDto);
    }

    @Test
    public void testDeleteBook() throws Exception {
        Long bookId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/books/" + bookId))
                .andExpect(status().isOk());

        verify(bookService).deleteBook(bookId);
    }

    @Test
    public void testDeleteBook_ResourceNotFoundException() throws Exception {
        Long bookId = 1L;
        doThrow(new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE)).when(bookService).deleteBook(bookId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/books/" + bookId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(RESOURCE_NOT_FOUND_MESSAGE)));

        verify(bookService).deleteBook(bookId);
    }


    private static PageResponseDto<BookDto> getPageResponseDto(List<BookDto> bookDtos) {
        return PageResponseDto.<BookDto>builder()
                .content(bookDtos)
                .build();
    }

    private static BookDto getBookDto() {
        return BookDto.builder()
                .id(1L)
                .title("Book1")
                .author("Author1")
                .build();
    }

    private static List<BookDto> getBookDtos() {
        List<BookDto> bookDtos = new ArrayList<>();
        BookDto bookDto1 = BookDto.builder()
                .id(1L)
                .title("Book1")
                .author("Author1")
                .build();
        bookDtos.add(bookDto1);

        BookDto bookDto2 = BookDto.builder()
                .id(2L)
                .title("Book2")
                .author("Author2")
                .build();
        bookDtos.add(bookDto2);
        return bookDtos;
    }
}
