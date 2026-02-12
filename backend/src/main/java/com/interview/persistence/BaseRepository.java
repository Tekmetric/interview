package com.interview.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T extends AuditMetadata> extends JpaRepository<T, Long> {

    Optional<T> findByUid(String uid);

    boolean existsByUid(String uid);

    void deleteByUid(String uid);
}
