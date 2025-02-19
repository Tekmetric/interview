package com.interview.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interview.models.Keyword;

@Repository
public interface IKeywordRepository extends JpaRepository<Keyword, Long> {

}
