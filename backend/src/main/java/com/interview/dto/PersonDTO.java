package com.interview.dto;

import lombok.Data;

@Data
public class PersonDTO {

    private Long id;

    private String name;

    private String email;

    private String password;

    private String newPasswordOne;

    private String newPasswordTwo;

    private String state;

    private String nickname;

}
