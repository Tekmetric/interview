package com.interview.repository;

import com.interview.entity.ReadingList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ReadingListRepository extends CrudRepository<ReadingList, Long> {

    @Query("SELECT r FROM ReadingList r WHERE r.shared = :shared " +
            "AND (:keyword IS NULL OR UPPER(r.name) LIKE %:keyword%)")
    Page<ReadingList> findAllBySharedAndKeyword(String keyword, boolean shared, Pageable pageable);

    @Query("SELECT r FROM ReadingList r WHERE r.owner.email = :email " +
            "AND (:keyword IS NULL OR UPPER(r.name) LIKE %:keyword%)")
    Page<ReadingList> findAllByKeywordAndOwnerEmail(String keyword, String email, Pageable pageable);
}
