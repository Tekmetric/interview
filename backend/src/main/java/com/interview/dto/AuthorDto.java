package com.interview.dto;

import com.interview.entity.Author;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class AuthorDto implements Serializable {
    private Long id;

    @NotBlank(message = "{error.required.author.firstName}")
    private String firstName;

    @NotBlank(message = "{error.required.author.lastName}")
    private String lastName;

    private String photoUrl;

    public AuthorDto(Author author) {
        this.id = author.getId();
        this.firstName = author.getFirstName();
        this.lastName = author.getLastName();
        this.photoUrl = author.getPhotoUrl();
    }
}
