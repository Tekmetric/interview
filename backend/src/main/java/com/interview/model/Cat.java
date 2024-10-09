package com.interview.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLInsert;
import org.springframework.lang.Nullable;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "cats")
@SQLInsert(sql = "INSERT INTO cats (age, fur_color, name, tag_line) VALUES ( ?, ?, ?, ? ) ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cat {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id = null;
    private String name;
    private Integer age;
    private String furColor;
    @Nullable
    private String tagLine;
}