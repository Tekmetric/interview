package com.interview.repository;

import com.interview.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Modifying
    @Query("DELETE FROM Review r where r.id = :id")
    void deleteById(Long id);
}
