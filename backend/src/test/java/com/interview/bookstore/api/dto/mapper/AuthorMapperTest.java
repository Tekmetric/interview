package com.interview.bookstore.api.dto.mapper;

import com.interview.bookstore.api.dto.AuthorDTO;
import com.interview.bookstore.domain.Author;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class AuthorMapperTest {

    private static final Long AUTHOR_ID = 1L;
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";

    @Test
    void mapDomainAuthorToAuthorDTO() {
        Author authorEntity = new Author();
        setField(authorEntity, "id", AUTHOR_ID);
        authorEntity.setFirstName(FIRST_NAME);
        authorEntity.setLastName(LAST_NAME);

        AuthorDTO authorDTO = AuthorMapper.toDTO(authorEntity);

        assertThat(authorDTO.getId()).isEqualTo(AUTHOR_ID);
        assertThat(authorDTO.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(authorDTO.getLastName()).isEqualTo(LAST_NAME);
    }

}
