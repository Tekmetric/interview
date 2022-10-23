package com.interview.domain.model;

import com.interview.domain.model.common.AbstractAuditingEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * User profile model class.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_profile")
@NoArgsConstructor
@Data
@Where(clause = "is_deleted is null")
public class UserProfile extends AbstractAuditingEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    public String computeInitials() {
        return String.valueOf(firstName.charAt(0)).toUpperCase() + String.valueOf(lastName.charAt(0)).toUpperCase();
    }

    public String computeDisplayName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", user=" + prettyPrintObject(user) +
                '}';
    }
}
