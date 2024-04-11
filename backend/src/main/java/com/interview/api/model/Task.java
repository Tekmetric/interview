package com.interview.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Data
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
public class Task {
    @Id
    @GeneratedValue//(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String description;

    @ManyToOne
    @JoinColumn(name="requester_id")
    private User requester;

    @ManyToOne
    @JoinColumn(name="assignee_id")
    private User assignee;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status = Status.OPEN;

    public Task(String title, String description, User requester, User assignee, Status status) {
        this.title = title;
        this.description = description;
        this.requester = requester;
        this.assignee = assignee;
        this.status = status;
    }
}
