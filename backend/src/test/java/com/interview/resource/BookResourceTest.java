package com.interview.resource;

import com.interview.dto.AuthorDto;
import com.interview.dto.BookDto;
import com.interview.dto.PaginatedBooksDto;
import com.interview.jwt.JwtService;
import com.interview.service.AuthorService;
import com.interview.service.BookService;
import com.interview.testutil.CommonTestConstants;
import com.interview.testutil.DtoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookResource.class)
@AutoConfigureMockMvc
class BookResourceTest {

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    private AuthorDto authorDto1;
    private AuthorDto authorDto2;
    private BookDto bookDto1;
    private BookDto bookDto2;


    @BeforeEach
    void setup() {
        authorDto1 = new AuthorDto(
                CommonTestConstants.ID_1,
                CommonTestConstants.FIRST_NAME_1,
                CommonTestConstants.LAST_NAME_1,
                CommonTestConstants.PHOTO_URL_1);

        authorDto2 = new AuthorDto(
                CommonTestConstants.ID_2,
                CommonTestConstants.FIRST_NAME_2,
                CommonTestConstants.LAST_NAME_2,
                CommonTestConstants.PHOTO_URL_2);

        bookDto1 = new BookDto(
                CommonTestConstants.ID_1,
                CommonTestConstants.NAME_1,
                authorDto1,
                CommonTestConstants.PUBLICATION_YEAR
        );

        bookDto2 = new BookDto(
                CommonTestConstants.ID_2,
                CommonTestConstants.NAME_2,
                authorDto1,
                CommonTestConstants.PUBLICATION_YEAR
        );
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void testGetAll() throws Exception {
        PaginatedBooksDto paginatedBooksDto = new PaginatedBooksDto();
        paginatedBooksDto.setCurrentPage(0);
        paginatedBooksDto.setTotalPages(1);
        paginatedBooksDto.setTotalItems(10);
        paginatedBooksDto.setBooks(Arrays.asList(bookDto1, bookDto2));

        when(bookService.getAll("name", PageRequest.of(0, 10))).thenReturn(paginatedBooksDto);

        mockMvc.perform(get("/api/books")
                        .param("keyword", "name")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalItems").value(10));
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void testGetById() throws Exception {
        when(bookService.findById(CommonTestConstants.ID_1)).thenReturn(bookDto1);

        mockMvc.perform(get("/api/books/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CommonTestConstants.ID_1))
                .andExpect(jsonPath("$.name").value(CommonTestConstants.NAME_1))
                .andExpect(jsonPath("$.author.id").value(authorDto1.getId()))
                .andExpect(jsonPath("$.author.firstName").value(authorDto1.getFirstName()))
                .andExpect(jsonPath("$.author.lastName").value(authorDto1.getLastName()))
                .andExpect(jsonPath("$.author.photoUrl").value(authorDto1.getPhotoUrl()))
                .andExpect(jsonPath("$.publicationYear").value(CommonTestConstants.PUBLICATION_YEAR));
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void testGetByIdNotFound() throws Exception {
        when(bookService.findById(3L)).thenReturn(null);

        mockMvc.perform(get("/api/books/3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void testSave() throws Exception {
        when(bookService.save(bookDto1)).thenReturn(bookDto1);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DtoUtil.toJson(bookDto1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CommonTestConstants.ID_1))
                .andExpect(jsonPath("$.name").value(CommonTestConstants.NAME_1))
                //.andExpect(jsonPath("$.author").value(CommonTestConstants.AUTHOR_1))
                .andExpect(jsonPath("$.publicationYear").value(CommonTestConstants.PUBLICATION_YEAR));
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void testUpdate() throws Exception {
        BookDto updatedBook = new BookDto(CommonTestConstants.ID_1, "Updated Book", authorDto2, 2025);

        when(bookService.save(updatedBook)).thenReturn(updatedBook);

        mockMvc.perform(put("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DtoUtil.toJson(updatedBook))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Book"))
                .andExpect(jsonPath("$.author.id").value(authorDto2.getId()))
                .andExpect(jsonPath("$.author.firstName").value(authorDto2.getFirstName()))
                .andExpect(jsonPath("$.author.lastName").value(authorDto2.getLastName()))
                .andExpect(jsonPath("$.author.photoUrl").value(authorDto2.getPhotoUrl()))
                .andExpect(jsonPath("$.publicationYear").value(2025));
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void testUpdateNotFound() throws Exception {
        BookDto updatedBook = new BookDto(3L, "Updated Book", authorDto2, 2025);

        when(bookService.save(updatedBook)).thenReturn(null);

        mockMvc.perform(put("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DtoUtil.toJson(bookDto1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void testDelete() throws Exception {
        doNothing().when(bookService).delete(CommonTestConstants.ID_1);

        mockMvc.perform(delete("/api/books/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).delete(CommonTestConstants.ID_1);
    }
}
