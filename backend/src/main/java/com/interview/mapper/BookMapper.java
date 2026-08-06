package com.interview.mapper;

import com.interview.repository.model.Book;
import com.interview.resource.model.BookDto.BookResponse;
import com.interview.resource.model.BookDto.CreateRequest;
import com.interview.resource.model.BookDto.UpdateRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Book toEntity(CreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateRequest request, @MappingTarget Book book);

    BookResponse toResponse(Book book);
}