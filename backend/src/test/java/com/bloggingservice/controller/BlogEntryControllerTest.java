package com.bloggingservice.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.bloggingservice.model.BlogEntryResponse;
import com.bloggingservice.service.BlogEntryService;
import java.security.Principal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class BlogEntryControllerTest {

  @Mock Principal principal;
  @Mock BlogEntryService blogEntryService;
  @InjectMocks BlogEntryController blogEntryController;

  @Test
  void shouldAcceptValidPaginationCall() {
    when(blogEntryService.getBlogEntries(any(), any()))
        .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 1), 1));

    Page<BlogEntryResponse> response =
        blogEntryController.getBlogEntries(principal, Pageable.unpaged());
    assertThat(response.getTotalPages(), equalTo(1));
  }

  @Test
  void shouldFailPaginationCallWithInvalidSortField() {
    ResponseStatusException ex =
        assertThrows(
            ResponseStatusException.class,
            () -> {
              blogEntryController.getBlogEntries(
                  principal, Pageable.unpaged(Sort.by("not a value")));
            });

    assertThat(ex.getMessage(), containsString("is not an acceptable sort field"));
  }
}
