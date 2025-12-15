package com.bloggingservice.model.mapper;

import com.bloggingservice.model.BlogEntryEntity;
import com.bloggingservice.model.BlogEntryResponse;
import com.bloggingservice.model.CreateBlogEntryRequest;
import com.bloggingservice.model.UpdateBlogEntryRequest;
import java.time.Instant;
import org.mapstruct.BeanMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BlogEntryMapper {

  @Mapping(source = "author", target = "id.author")
  BlogEntryEntity fromCreateRequest(String author, CreateBlogEntryRequest request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  BlogEntryEntity fromUpdateRequest(
      @MappingTarget BlogEntryEntity entity, UpdateBlogEntryRequest request);

  @Mapping(source = "id.id", target = "id")
  BlogEntryResponse toBlogEntryResponse(BlogEntryEntity entity);

  // Handle edge case where only the categories are modified in a request
  @BeforeMapping
  default void lastUpdatedTimestampCategorySync(
      @MappingTarget BlogEntryEntity entity, UpdateBlogEntryRequest request) {
    if (request.categories() == null) {
      return;
    }

    if (!request.categories().equals(entity.getCategories())) {
      entity.setLastUpdateTimestamp(Instant.now());
    }
  }
}
