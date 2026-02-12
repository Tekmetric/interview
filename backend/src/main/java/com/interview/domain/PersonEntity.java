package com.interview.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.interview.converter.LowercaseConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "PERSON", indexes = {@Index(name = "person_email_uni", columnList = "P_EMAIL", unique = true)})
public class PersonEntity extends AbstractEntity {

    @Column(name = "P_NAME", nullable = false)
    @NotBlank
    private String name;

    @Email
    @NotBlank
    @Column(name = "P_EMAIL", nullable = false)
    @Convert(converter = LowercaseConverter.class)
    private String email;

    @Column(name = "P_PASSWORD", nullable = false)
    @JsonIgnore //just in case someone returns this object in a RestController
    //TODO: Converter to encrypt/decrypt password
    private String password;

    @Column(name = "P_STATE", length = 2)
    private String state;

    @Column(name = "P_NICKNAME")
    private String nickname;

}
