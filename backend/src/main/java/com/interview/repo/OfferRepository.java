package com.interview.repo;

import com.interview.model.domain.OfferEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OfferRepository extends JpaRepository<OfferEntity, UUID> {

  @Query(value = "SELECT o FROM OfferEntity o WHERE o.listing.listingId = :listingId")
  List<OfferEntity> findByListingId(@Param("listingId") UUID listingId);
}
