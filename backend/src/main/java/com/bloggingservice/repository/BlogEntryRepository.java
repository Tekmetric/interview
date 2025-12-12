package com.bloggingservice.repository;

import com.bloggingservice.model.BlogEntryEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface BlogEntryRepository extends CrudRepository<BlogEntryEntity, UUID> {}
