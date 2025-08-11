package com.interview.jpa.entity;

import com.interview.jpa.entity.common.AuditFields;
import com.interview.jpa.entity.enums.UserEnum;
import com.interview.jpa.entity.enums.UserEnum.Role;
import com.interview.jpa.entity.enums.UserEnum.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@Table(name = "app_user")
@DynamicUpdate
@AllArgsConstructor
public class User extends AuditFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "full_name")
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "password_hash")
    private String passwordHash;
}