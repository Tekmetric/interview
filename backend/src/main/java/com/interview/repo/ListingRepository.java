package com.interview.repo;

import com.interview.model.domain.ListingEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingRepository extends JpaRepository<ListingEntity, UUID> {}
