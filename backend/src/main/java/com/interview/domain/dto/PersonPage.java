package com.interview.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * Serialization-friendly {@link Page} implementation for Persons.
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = {"pageable"})
public final class PersonPage extends PageImpl<Person> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public PersonPage(
            @JsonProperty("content") List<Person> content,
            @JsonProperty("number") int page,
            @JsonProperty("size") int size,
            @JsonProperty("totalElements") long total
    ) {
        super(content, PageRequest.of(page, size), total);
    }
}
