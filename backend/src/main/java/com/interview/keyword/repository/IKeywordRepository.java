package com.interview.keyword.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interview.keyword.model.Keyword;

@Repository
public interface IKeywordRepository extends JpaRepository<Keyword, Long> {
    Optional<Keyword> findByName(String name);

}
