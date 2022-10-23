package com.interview.domain.model;

import com.interview.domain.model.common.AbstractAuditingEntity;
import com.interview.domain.model.enums.UserRelationshipState;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * User relation model class.
 */
@Entity
@Where(clause = "is_deleted is null")
@Table(name = "user_relationship")
@Getter
@Setter
public class UserRelationship extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The User who initiated the friend request
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_user_id", nullable = false)
    private User sender;

    /**
     * The User who received the friend request
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_user_id", nullable = false)
    private User receiver;


    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private UserRelationshipState state;

    public UserRelationship() {
    }

    @Override
    public String toString() {
        return "UserRelationship{" +
                "id=" + id +
                ", sender=" + prettyPrintObject(sender) +
                ", receiver=" + prettyPrintObject(receiver) +
                ", state=" + state +
                '}';
    }
}
