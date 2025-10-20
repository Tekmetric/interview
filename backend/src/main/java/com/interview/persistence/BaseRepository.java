package com.interview.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T extends AuditMetadata> extends JpaRepository<T, Long> {

    T findByUid(String uid);

    boolean existsByUid(String uid);

    void deleteByUid(String uid);
}
