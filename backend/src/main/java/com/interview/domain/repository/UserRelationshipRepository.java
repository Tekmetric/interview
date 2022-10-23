package com.interview.domain.repository;

import com.interview.domain.model.UserRelationship;
import com.interview.domain.model.enums.UserRelationshipState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRelationshipRepository extends EntityRepository<UserRelationship, Long> {

    @Query("select ur from UserRelationship ur " +
            "where (((ur.sender.id = :senderUserId and ur.receiver.id = :receiverUserId) " +
            "or (ur.sender.id = :receiverUserId and ur.receiver.id = :senderUserId)) " +
            "and (ur.deleted is null))")
    Optional<UserRelationship> findUserRelationshipByTwoUserIds(@Param("senderUserId") Long senderUserId, @Param("receiverUserId") Long receiverUserId);

    @Query("SELECT ur " +
            "FROM UserRelationship ur " +
            "JOIN UserProfile up1 ON up1.user.id = ur.receiver.id AND up1.deleted IS NULL " +
            "JOIN UserProfile up2 ON up2.user.id = ur.sender.id AND up2.deleted IS NULL " +
            "WHERE (ur.sender.id = :userId or ur.receiver.id = :userId) " +
            "AND ur.state = :state " +
            "AND ur.deleted IS NULL " +
            "AND (:search IS NULL OR (up1.firstName LIKE %:search% OR up1.lastName LIKE %:search% or up2.firstName LIKE %:search% OR up2.lastName LIKE %:search%)) " +
            "ORDER BY ur.id")
    Slice<UserRelationship> getFriends(Long userId, UserRelationshipState state, String search, Pageable pageable);


}
