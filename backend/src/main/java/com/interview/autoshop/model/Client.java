package com.interview.autoshop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //We may want to separate this into firstName and lastName etc.
    private String name;

    //In real world, we should create a separate entity to store address, with line1, line2, city, state, zip etc.
    private String address;

    private String phone;

    @Email
    private String email;

}
