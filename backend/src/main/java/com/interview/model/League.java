package com.interview.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "league")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private String skillLevel;
    @OneToMany(mappedBy = "league", cascade = CascadeType.PERSIST)
    @JsonManagedReference
    private List<Team> teams;

    public League(Long id) {
        this.id = id;
    }
}
