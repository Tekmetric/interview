package com.bloggingservice.model.mapper;

import com.bloggingservice.model.BlogEntryEntity;
import com.bloggingservice.model.CreateBlogEntryRequest;
import com.bloggingservice.model.BlogEntryResponse;
import com.bloggingservice.model.UpdateBlogEntryRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BlogEntryMapper {

    BlogEntryEntity fromCreateRequest(CreateBlogEntryRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    BlogEntryEntity fromUpdateRequest(@MappingTarget BlogEntryEntity entity, UpdateBlogEntryRequest request);

    BlogEntryResponse toBlogEntryResponse(BlogEntryEntity entity);
}
