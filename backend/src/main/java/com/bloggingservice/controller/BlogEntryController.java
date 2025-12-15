package com.bloggingservice.controller;

import com.bloggingservice.model.BlogEntryId;
import com.bloggingservice.model.BlogEntryResponse;
import com.bloggingservice.model.CreateBlogEntryRequest;
import com.bloggingservice.model.UpdateBlogEntryRequest;
import com.bloggingservice.service.BlogEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.security.Principal;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/blog-entry", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
@Tag(name = "Blog Entries", description = "Operations for blog entries")
@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
public class BlogEntryController {

  private static final Set<String> VALID_PAGE_SORT_FIELDS = Set.of("creationTimestamp");

  private final BlogEntryService blogEntryService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Create a blog entry",
      description = "Create a new blog entry for the authorized user")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Blog entry created successfully",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = BlogEntryResponse.class))
            })
      })
  public BlogEntryResponse createBlogEntry(
      Principal principal, @Valid @RequestBody CreateBlogEntryRequest request) {
    return blogEntryService.createBlogEntry(principal.getName(), request);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Get blog entries",
      description = "Get a list of blog entries for the authorized user")
  @PageableAsQueryParam
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Blog entries returned successfully",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = Page.class))
            })
      })
  public Page<BlogEntryResponse> getBlogEntries(
      Principal principal, @Parameter(hidden = true) Pageable pageable) {
    pageable.getSort().stream()
        .forEach(
            order -> {
              if (!VALID_PAGE_SORT_FIELDS.contains(order.getProperty())) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format(
                        "%s is not an acceptable sort field from %s",
                        order.getProperty(), VALID_PAGE_SORT_FIELDS));
              }
            });
    return blogEntryService.getBlogEntries(principal.getName(), pageable);
  }

  @GetMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Get a blog entry",
      description = "Get a specific blog entry for the authorized user")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Blog entry found successfully",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = BlogEntryResponse.class))
            }),
        @ApiResponse(responseCode = "404", description = "Blog entry not found", content = @Content)
      })
  public BlogEntryResponse getBlogEntry(Principal principal, @PathVariable UUID id)
      throws NoResourceFoundException {
    return blogEntryService.getBlogEntry(new BlogEntryId(id, principal.getName()));
  }

  @PatchMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Update a blog entry",
      description = "Update a specific blog entry for the authorized user")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Blog entry updated successfully",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = BlogEntryResponse.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "Blog entry not found",
            content = @Content),
        @ApiResponse(
            responseCode = "409",
            description = "Blog entry update conflicted with another update",
            content = @Content)
      })
  public BlogEntryResponse updateBlogEntry(
      Principal principal,
      @PathVariable UUID id,
      @Valid @RequestBody UpdateBlogEntryRequest request)
      throws NoResourceFoundException {
    return blogEntryService.updateBlogEntry(new BlogEntryId(id, principal.getName()), request);
  }

  @DeleteMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Delete a blog entry",
      description = "Delete a specific blog entry for the authorized user")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "Blog entry deleted successfully",
            content = @Content)
      })
  public void removeBlogEntry(Principal principal, @PathVariable UUID id) {
    blogEntryService.removeBlogEntry(new BlogEntryId(id, principal.getName()));
  }
}
