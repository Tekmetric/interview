package com.interview.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "author")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    @ManyToMany
    private Set<Book> books;
}
