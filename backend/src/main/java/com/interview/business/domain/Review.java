package com.interview.business.domain;

import com.interview.core.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Range;

@Entity
@Table(name = "reviews")
@FieldNameConstants
@SuperBuilder
@NoArgsConstructor
public class Review extends BaseEntity {

    @NotNull
    @Column(name = "user_id", nullable = false, updatable = false)
    public String userId;

    @NotNull
    @Column(name = "recipe_id", nullable = false, updatable = false)
    public String recipeId;

    @NotNull
    @Size(min = 1, max = 2000)
    @Column(name = "message", nullable = false)
    public String message;

    @NotNull
    @Range(min = 1)
    @Column(name = "rating", nullable = false)
    public Integer rating;

    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @ManyToOne(targetEntity = AppUser.class, fetch = FetchType.EAGER)
    public AppUser user;

    @JoinColumn(name = "recipe_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Recipe recipe;
}
