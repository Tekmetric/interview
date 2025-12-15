package com.bloggingservice.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.bloggingservice.model.BlogEntryId;
import com.bloggingservice.model.UpdateBlogEntryRequest;
import com.bloggingservice.model.mapper.BlogEntryMapper;
import com.bloggingservice.repository.BlogEntryRepository;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ExtendWith(MockitoExtension.class)
class BlogEntryServiceImplTest {

  @Mock Principal principal;
  @Mock private BlogEntryMapper blogEntryMapper;
  @Mock private BlogEntryRepository blogEntryRepository;
  @InjectMocks private BlogEntryServiceImpl blogEntryService;

  private BlogEntryId id;

  @BeforeEach
  void setup() {
    id = new BlogEntryId(UUID.randomUUID(), principal.getName());
  }

  @Test
  void shouldExceptForUnfoundResourceOnGet() {
    when(blogEntryRepository.findById(any())).thenReturn(Optional.empty());

    NoResourceFoundException ex =
        assertThrows(NoResourceFoundException.class, () -> blogEntryService.getBlogEntry(id));
    assertThat(ex.getHttpMethod(), equalTo(HttpMethod.GET));
    assertThat(ex.getResourcePath(), equalTo("/api/v1/blog-entry/{id}"));
  }

  @Test
  void shouldExceptForUnfoundResourceOnUpdate() {
    when(blogEntryRepository.findById(any())).thenReturn(Optional.empty());

    NoResourceFoundException ex =
        assertThrows(
            NoResourceFoundException.class,
            () -> blogEntryService.updateBlogEntry(id, new UpdateBlogEntryRequest(null, null)));
    assertThat(ex.getHttpMethod(), equalTo(HttpMethod.PATCH));
    assertThat(ex.getResourcePath(), equalTo("/api/v1/blog-entry/{id}"));
  }
}
