package com.bloggingservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(
    name = "Update Blog Entry Request",
    description = "The request body used to update a blog entry")
public record UpdateBlogEntryRequest(String content, String title, Set<CategoryType> categories) {}
