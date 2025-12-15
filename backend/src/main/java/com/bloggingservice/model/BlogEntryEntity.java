package com.bloggingservice.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
    name = "Blog_Entry",
    indexes = {@Index(name = "idx_author", columnList = "author")})
@Getter
@Setter
@SoftDelete
public class BlogEntryEntity {

  @EmbeddedId private BlogEntryId id;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Instant creationTimestamp;

  @UpdateTimestamp
  @Column(nullable = false)
  private Instant lastUpdateTimestamp;

  @NotNull
  @Lob
  @Column(nullable = false)
  private String content;

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
  private Set<CategoryType> categories = new HashSet<>();

  @Version
  @Column(nullable = false)
  private Long version;
}
