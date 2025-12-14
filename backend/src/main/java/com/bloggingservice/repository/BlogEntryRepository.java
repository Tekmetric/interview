package com.bloggingservice.repository;

import com.bloggingservice.model.BlogEntryEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import java.util.UUID;

public interface BlogEntryRepository
        extends ListPagingAndSortingRepository<BlogEntryEntity, UUID>, CrudRepository<BlogEntryEntity, UUID> {}
