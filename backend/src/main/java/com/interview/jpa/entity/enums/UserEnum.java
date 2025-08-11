package com.interview.jpa.entity.enums;

public interface UserEnum {

    enum Role {
        ADMIN, DISPATCHER, PILOT, CREW
    }

    enum Status {
        ACTIVE, INACTIVE
    }
}
