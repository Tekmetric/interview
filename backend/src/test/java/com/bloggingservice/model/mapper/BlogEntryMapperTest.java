package com.bloggingservice.model.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import com.bloggingservice.model.BlogEntryEntity;
import com.bloggingservice.model.CategoryType;
import com.bloggingservice.model.UpdateBlogEntryRequest;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class BlogEntryMapperTest {
  private final BlogEntryMapper mapper = Mappers.getMapper(BlogEntryMapper.class);

  @Test
  void shouldUpdateTimestampWhenCategoriesChange() {
    BlogEntryEntity entity = new BlogEntryEntity();
    entity.setCategories(Set.of());
    UpdateBlogEntryRequest request =
        new UpdateBlogEntryRequest("Test content", Set.of(CategoryType.SCIENCE));

    mapper.lastUpdatedTimestampCategorySync(entity, request);
    assertThat(entity.getLastUpdateTimestamp(), not(nullValue()));
  }

  @Test
  void shouldNotUpdateTimestampWhenCategoriesIsNotSpecified() {
    BlogEntryEntity entity = new BlogEntryEntity();
    entity.setCategories(Set.of());
    UpdateBlogEntryRequest request = new UpdateBlogEntryRequest("Test content", null);

    mapper.lastUpdatedTimestampCategorySync(entity, request);
    assertThat(entity.getLastUpdateTimestamp(), nullValue());
  }

  @Test
  void shouldNotUpdateTimestampWhenCategoriesAreTheSame() {
    Set<CategoryType> categories = Set.of(CategoryType.SCIENCE);
    BlogEntryEntity entity = new BlogEntryEntity();
    entity.setCategories(categories);
    UpdateBlogEntryRequest request = new UpdateBlogEntryRequest("Test content", categories);

    mapper.lastUpdatedTimestampCategorySync(entity, request);
    assertThat(entity.getLastUpdateTimestamp(), nullValue());
  }
}
