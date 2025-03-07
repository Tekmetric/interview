package com.interview.resource;

import com.interview.dto.*;
import com.interview.jwt.JwtService;
import com.interview.service.ReadingListService;
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

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReadingListResource.class)
@AutoConfigureMockMvc
class ReadingListResourceTest {

    @MockBean
    private ReadingListService readingListService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    private ReadingListDto readingListDto1;
    private PaginatedReadingListDto paginatedReadingListDto;

    private BookDto bookDto1;
    private BookDto bookDto2;

    private UserDto userDto1;

    @BeforeEach
    void setup() {
        AuthorDto authorDto1 = new AuthorDto(
                CommonTestConstants.ID_1,
                CommonTestConstants.FIRST_NAME_1,
                CommonTestConstants.LAST_NAME_1,
                CommonTestConstants.PHOTO_URL_1);

        AuthorDto authorDto2 = new AuthorDto(
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

        BookDto bookDto3 = new BookDto(
                CommonTestConstants.ID_3,
                CommonTestConstants.NAME_3,
                authorDto2,
                CommonTestConstants.PUBLICATION_YEAR
        );

        BookDto bookDto4 = new BookDto(
                CommonTestConstants.ID_4,
                CommonTestConstants.NAME_4,
                authorDto2,
                CommonTestConstants.PUBLICATION_YEAR
        );

        userDto1 = new UserDto(
             CommonTestConstants.ID_1,
             CommonTestConstants.FIRST_NAME_1,
             CommonTestConstants.LAST_NAME_1,
             CommonTestConstants.EMAIL_1,
             false
        );

        readingListDto1 = new ReadingListDto(
                CommonTestConstants.ID_1,
                CommonTestConstants.NAME_1,
                userDto1,
                CommonTestConstants.SHARED_DATE,
                true,
                Arrays.asList(bookDto1, bookDto2));

        ReadingListDto readingListDto2 = new ReadingListDto(
                CommonTestConstants.ID_2,
                CommonTestConstants.NAME_2,
                userDto1,
                CommonTestConstants.SHARED_DATE,
                true,
                Arrays.asList(bookDto3, bookDto4));

        paginatedReadingListDto = new PaginatedReadingListDto();
        paginatedReadingListDto.setCurrentPage(0);
        paginatedReadingListDto.setTotalPages(1);
        paginatedReadingListDto.setTotalItems(2);
        paginatedReadingListDto.setReadingLists(Arrays.asList(readingListDto1, readingListDto2));
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    void testGetAll() throws Exception {
        when(readingListService.getAll("name", "user", PageRequest.of(0, 10))).thenReturn(paginatedReadingListDto);

        mockMvc.perform(get("/api/reading-lists")
                        .param("keyword", "name")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalItems").value(2));
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    void testGetById() throws Exception {
        when(readingListService.findById(CommonTestConstants.ID_1)).thenReturn(readingListDto1);

        mockMvc.perform(get("/api/reading-lists/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CommonTestConstants.ID_1))
                .andExpect(jsonPath("$.name").value(CommonTestConstants.NAME_1))
                .andExpect(jsonPath("$.owner.firstName").value(CommonTestConstants.FIRST_NAME_1))
                .andExpect(jsonPath("$.owner.lastName").value(CommonTestConstants.LAST_NAME_1))
                .andExpect(jsonPath("$.owner.email").value(CommonTestConstants.EMAIL_1));
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    void testGetByIdNotFound() throws Exception {
        when(readingListService.findById(3L)).thenReturn(null);

        mockMvc.perform(get("/api/reading-lists/3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = CommonTestConstants.EMAIL_1, authorities = {"USER"})
    @Test
    void testSave() throws Exception {
        ReadingListRequestDto requestDto = new ReadingListRequestDto(
                null,
                "Reading List 3",
                false,
                new ArrayList<Long>());
        when(readingListService.save(any(), any())).thenReturn(readingListDto1);

        mockMvc.perform(post("/api/reading-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DtoUtil.toJson(requestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CommonTestConstants.ID_1))
                .andExpect(jsonPath("$.name").value(CommonTestConstants.NAME_1));
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    void testUpdate() throws Exception {
        ReadingListDto updatedReadingListDto = new ReadingListDto(
                CommonTestConstants.ID_1,
                "Updated Reading List",
                userDto1,
                CommonTestConstants.SHARED_DATE,
                false,
                Arrays.asList(bookDto1, bookDto2));

        when(readingListService.save(any(), any())).thenReturn(updatedReadingListDto);

        mockMvc.perform(put("/api/reading-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DtoUtil.toJson(updatedReadingListDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CommonTestConstants.ID_1))
                .andExpect(jsonPath("$.name").value("Updated Reading List"))
                .andExpect(jsonPath("$.owner.firstName").value(CommonTestConstants.FIRST_NAME_1))
                .andExpect(jsonPath("$.owner.lastName").value(CommonTestConstants.LAST_NAME_1));
    }

    @WithMockUser(username = CommonTestConstants.EMAIL_1, authorities = {"USER"})
    @Test
    void testUpdateNotFound() throws Exception {
        ReadingListDto updatedReadingListDto = new ReadingListDto(
                CommonTestConstants.ID_1,
                "Updated Reading List",
                userDto1,
                CommonTestConstants.SHARED_DATE,
                false,
                Arrays.asList(bookDto1, bookDto2));
        when(readingListService.save(any(), any())).thenReturn(null);

        mockMvc.perform(put("/api/reading-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DtoUtil.toJson(updatedReadingListDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = CommonTestConstants.EMAIL_1, authorities = {"USER"})
    @Test
    void testDelete() throws Exception {
        doNothing().when(readingListService).delete(CommonTestConstants.ID_1, CommonTestConstants.EMAIL_1);

        mockMvc.perform(delete("/api/reading-lists/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(readingListService, times(1)).delete(CommonTestConstants.ID_1, CommonTestConstants.EMAIL_1);
    }

    @Test
    void testGetAllShared() throws Exception {
        when(readingListService.getAllShared("keyword", PageRequest.of(0, 10))).thenReturn(paginatedReadingListDto);

        mockMvc.perform(get("/api/reading-lists/shared")
                        .param("keyword", "keyword")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalItems").value(2));
    }
}
