package com.bloggingservice.model.mapper;

import com.bloggingservice.model.BlogEntryEntity;
import com.bloggingservice.model.CreateBlogEntryRequest;
import com.bloggingservice.model.CreateBlogEntryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BlogEntryMapper {

    BlogEntryEntity fromCreateRequest(CreateBlogEntryRequest request);
    CreateBlogEntryResponse toBlogEntryResponse(BlogEntryEntity entity);
}
