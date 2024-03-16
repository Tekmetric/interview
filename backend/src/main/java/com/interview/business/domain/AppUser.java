package com.interview.business.domain;

import com.interview.core.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = AppUser.Fields.email)
})
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AppUser extends BaseEntity {
    @NotNull
    @Size(min = 3)
    @Column(name = "name", nullable = false)
    public String name;

    @NotNull
    @Email
    @Column(name = "email", nullable = false, unique = true)
    public String email;

    @NotNull
    @Column(name = "password", nullable = false)
    public String password;

    @NotNull
    @Column(name = "avatar", nullable = false)
    public String avatar;

}
