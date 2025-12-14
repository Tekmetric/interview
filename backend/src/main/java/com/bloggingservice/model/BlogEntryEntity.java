package com.bloggingservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "Blog_Entry")
@Getter
@Setter
@SoftDelete
public class BlogEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private Instant creationTimestamp;

    @NotNull
    private String content;

    @Version
    private Long version;
}
