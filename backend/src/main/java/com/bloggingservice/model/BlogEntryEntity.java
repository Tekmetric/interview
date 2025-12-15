package com.bloggingservice.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@IdClass(BlogEntryId.class)
@Table(
    name = "Blog_Entry",
    indexes = {@Index(name = "idx_author", columnList = "author")})
@Getter
@Setter
@SoftDelete
public class BlogEntryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Id private String author;

  @NotNull private Instant creationTimestamp;

  @NotNull @Lob private String content;

  // For now, we'll eager fetch given that the defined categories are limited.
  // This relationship should be modeled differently if it is unbounded
  @ElementCollection(targetClass = CategoryType.class, fetch = FetchType.EAGER)
  @CollectionTable(
      name = "Categories",
      joinColumns = {
        @JoinColumn(name = "blog_entry_id", referencedColumnName = "id"),
        @JoinColumn(name = "blog_entry_author", referencedColumnName = "author")
      })
  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false)
  @SoftDelete
  private Set<CategoryType> categories = new HashSet<>();

  @Version private Long version;
}
