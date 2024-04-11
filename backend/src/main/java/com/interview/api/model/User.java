package com.interview.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Entity
@Table(name="users")
public class User {
    @Id @GeneratedValue
    //@JdbcTypeCode(Types.VARCHAR)
    private Long id;

    @NotNull
    @Column(unique=true)
    private String username;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String password;

    @OneToMany(mappedBy = "assignee", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Task> assignedTasks;

    @OneToMany(mappedBy = "requester", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Task> requesterTasks;
}
