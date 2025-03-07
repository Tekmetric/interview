package com.interview.resource;

import com.interview.dto.AuthorDto;
import com.interview.dto.BookDto;
import com.interview.dto.PaginatedAuthorsDto;
import com.interview.jwt.JwtService;
import com.interview.service.AuthorService;
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

@WebMvcTest(AuthorResource.class)
@AutoConfigureMockMvc
class AuthorResourceTest {

    @MockBean
    private AuthorService authorService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    private AuthorDto authorDto1;
    private AuthorDto authorDto2;

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

    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void testGetAll() throws Exception {
        PaginatedAuthorsDto paginatedAuthorsDto = new PaginatedAuthorsDto();
        paginatedAuthorsDto.setCurrentPage(0);
        paginatedAuthorsDto.setTotalPages(1);
        paginatedAuthorsDto.setTotalItems(10);
        paginatedAuthorsDto.setAuthors(Arrays.asList(authorDto1, authorDto2));

        when(authorService.getAll("author", PageRequest.of(0, 10))).thenReturn(paginatedAuthorsDto);

        mockMvc.perform(get("/api/authors")
                        .param("keyword", "author")
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
        when(authorService.findById(1L)).thenReturn(authorDto1);

        mockMvc.perform(get("/api/authors/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CommonTestConstants.ID_1))
                .andExpect(jsonPath("$.firstName").value(CommonTestConstants.FIRST_NAME_1))
                .andExpect(jsonPath("$.lastName").value(CommonTestConstants.LAST_NAME_1))
                .andExpect(jsonPath("$.photoUrl").value(CommonTestConstants.PHOTO_URL_1));
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void testGetByIdNotFound() throws Exception {
        when(authorService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/authors/3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void testGetBooksById() throws Exception {
        BookDto bookDto1 = new BookDto(
                CommonTestConstants.ID_1,
                CommonTestConstants.NAME_1,
                authorDto1,
                CommonTestConstants.PUBLICATION_YEAR);

        BookDto bookDto2 = new BookDto(
                CommonTestConstants.ID_2,
                CommonTestConstants.NAME_2,
                authorDto1,
                CommonTestConstants.PUBLICATION_YEAR);

        when(authorService.findById(CommonTestConstants.ID_1)).thenReturn(authorDto1);
        when(authorService.findBooksOfAuthorById(CommonTestConstants.ID_1)).thenReturn(Arrays.asList(bookDto1, bookDto2));

        mockMvc.perform(get("/api/authors/1/books")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void testGetBooksByIdNotFound() throws Exception {
        when(authorService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/authors/3/books")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void testSave() throws Exception {
        when(authorService.save(authorDto1)).thenReturn(authorDto1);

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DtoUtil.toJson(authorDto1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CommonTestConstants.ID_1))
                .andExpect(jsonPath("$.firstName").value(CommonTestConstants.FIRST_NAME_1))
                .andExpect(jsonPath("$.lastName").value(CommonTestConstants.LAST_NAME_1))
                .andExpect(jsonPath("$.photoUrl").value(CommonTestConstants.PHOTO_URL_1));
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void testUpdate() throws Exception {
        AuthorDto authorDto = new AuthorDto(CommonTestConstants.ID_1, "Updated", "Author", "new photoUrl");

        when(authorService.update(authorDto)).thenReturn(authorDto);

        mockMvc.perform(put("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DtoUtil.toJson(authorDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Author"))
                .andExpect(jsonPath("$.photoUrl").value("new photoUrl"));
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void testUpdateNotFound() throws Exception {
        AuthorDto authorDto = new AuthorDto(3L, "Updated", "Author", "new photoUrl");

        when(authorService.update(authorDto)).thenReturn(null);

        mockMvc.perform(put("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DtoUtil.toJson(authorDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    void testDelete() throws Exception {
        doNothing().when(authorService).delete(1L);

        mockMvc.perform(delete("/api/authors/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(authorService, times(1)).delete(1L);
    }
}
