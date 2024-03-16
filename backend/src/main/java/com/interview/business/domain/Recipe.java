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
@Table(name = "recipes")
@FieldNameConstants
@SuperBuilder
@NoArgsConstructor
public class Recipe extends BaseEntity {

    @NotNull
    @Column(name = "user_id", nullable = false, updatable = false)
    public String userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false, updatable = false)
    public MealType mealType;

    @NotNull
    @Column(name = "image", nullable = false)
    public String image;

    @NotNull
    @Size(min = 5, max = 64)
    @Column(name = "title", nullable = false)
    public String title;

    @NotNull
    @Size(min = 1, max = 20000)
    @Column(name = "description", nullable = false, length = 2000)
    public String description;

    @NotNull
    @Range(min = 1)
    @Column(name = "duration", nullable = false)
    public Integer duration;

    @NotNull
    @Range(min = 0, max = 5)
    @Column(name = "rating_average", nullable = false)
    public Double ratingAverage;

    @NotNull
    @Range(min = 0)
    @Column(name = "rating_count", nullable = false)
    public Long ratingCount;

    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @ManyToOne(targetEntity = AppUser.class, fetch = FetchType.EAGER)
    public AppUser user;

    public enum MealType {
        BREAKFAST,
        LUNCH,
        SALAD,
        SOUP,
        BREAD,
        DESSERT,
    }
}
