package com.interview.repository;

import com.interview.model.JobStatus;
import com.interview.model.db.Job;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;


public interface JobRepository extends CrudRepository<Job, Integer> {
    // Use custom update in order to avoid select before update full entity
    @Modifying
    @Transactional
    @Query(value = """
            UPDATE Job j
            SET j.status = :status, j.scheduledAt = :scheduledAt, j.updatedAt = cast(current_timestamp as instant)
            WHERE j.id = :id""")
    int updateJob(@Param("id") Integer id,
                  @Param("status") JobStatus status,
                  @Param("scheduledAt") Instant scheduledAt);

    // Override default deleteById in order to return result & remove unnecessary select done before the delete
    @Modifying
    @Transactional
    @Query(value = """
                    DELETE from Job j where j.id = :id
            """)
    int deleteJobById(@Param("id") Integer id);

    List<Job> findAllByCar_VinOrderByScheduledAtDesc(String vin);

    @Query(value = """
            SELECT j.id
            FROM Job j
            WHERE j.status = :status
            """,
            countQuery = """
                    SELECT count(j)
                    FROM Job j
                    WHERE j.status = :status""")
    List<Long> findAllByStatus(
            @Param("status") JobStatus status,
            Pageable pageable);

    @Query("""
            select j
            from Job j
            where j.id in :jobIds
            """
    )
    List<Job> findAllByIdWithTasks(
            List<Long> jobIds
    );
}
