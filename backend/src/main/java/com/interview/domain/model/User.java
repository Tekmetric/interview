package com.interview.domain.model;

import com.interview.domain.model.common.AbstractAuditingEntity;
import com.interview.domain.model.enums.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * User model class.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
@Where(clause = "is_deleted is null")
public class User extends AbstractAuditingEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50, nullable = false)
    private String email;
    @Column(length = 60)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private UserProfile userProfile;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", userProfile=" + prettyPrintObject(userProfile) +
                '}';
    }
}
