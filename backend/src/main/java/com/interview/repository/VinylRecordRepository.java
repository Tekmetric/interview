package com.interview.repository;

import com.interview.domain.VinylRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VinylRecordRepository extends JpaRepository<VinylRecord, Long>, JpaSpecificationExecutor<VinylRecord> {

    @Query(value = "SELECT COUNT(*) FROM vinyl_record vr" +
            "       INNER JOIN vinyl_record_artist vra ON vr.id = vra.record_id" +
            "       WHERE vr.title = :title AND vra.artists_id IN (:artists)", nativeQuery = true)
    int countAlbumsAlreadyInsertedWithSameCombination(@Param("title") String title, @Param("artists") List<Long> artists);
}
