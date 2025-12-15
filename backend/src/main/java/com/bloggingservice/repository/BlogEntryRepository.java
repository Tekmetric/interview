package com.bloggingservice.repository;

import com.bloggingservice.model.BlogEntryEntity;
import com.bloggingservice.model.BlogEntryId;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface BlogEntryRepository
    extends ListPagingAndSortingRepository<BlogEntryEntity, BlogEntryId>,
        CrudRepository<BlogEntryEntity, BlogEntryId> {

  @Query("SELECT BE.id.id FROM BlogEntryEntity BE WHERE BE.id.author = :author")
  Page<UUID> findAllIdsByAuthor(@Param("author") String author, Pageable page);

  @Override
  @EntityGraph(attributePaths = "categories")
  @Nonnull
  List<BlogEntryEntity> findAllById(@Nonnull Iterable<BlogEntryId> ids);
}
