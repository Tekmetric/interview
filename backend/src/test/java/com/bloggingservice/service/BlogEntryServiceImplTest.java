package com.bloggingservice.service;

import com.bloggingservice.model.UpdateBlogEntryRequest;
import com.bloggingservice.model.mapper.BlogEntryMapper;
import com.bloggingservice.repository.BlogEntryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlogEntryServiceImplTest {

    @Mock
    private BlogEntryMapper blogEntryMapper;
    @Mock
    private BlogEntryRepository blogEntryRepository;
    @InjectMocks
    private BlogEntryServiceImpl blogEntryService;

    @Test
    void shouldExceptForUnfoundResourceOnGet() {
        when(blogEntryRepository.findById(any())).thenReturn(Optional.empty());

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () ->
           blogEntryService.getBlogEntry(UUID.randomUUID()));
        assertThat(ex.getHttpMethod(), equalTo(HttpMethod.GET));
        assertThat(ex.getResourcePath(), equalTo("/api/v1/blog-entry/{id}"));
    }

    @Test
    void shouldExceptForUnfoundResourceOnUpdate() {
        when(blogEntryRepository.findById(any())).thenReturn(Optional.empty());

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () ->
                blogEntryService.updateBlogEntry(UUID.randomUUID(), new UpdateBlogEntryRequest(null, null)));
        assertThat(ex.getHttpMethod(), equalTo(HttpMethod.PATCH));
        assertThat(ex.getResourcePath(), equalTo("/api/v1/blog-entry/{id}"));
    }
}