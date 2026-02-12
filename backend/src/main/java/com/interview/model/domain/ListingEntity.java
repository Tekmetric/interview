package com.interview.model.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.interview.model.common.PropertyType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "listings")
@Getter
@Setter
public class ListingEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "listing_id", nullable = false)
  private UUID listingId;

  @Column(name = "address", nullable = false)
  private String address;

  @Column(name = "agent_name", nullable = false)
  private String agentName;

  @Enumerated(EnumType.STRING)
  @Column(name = "property_type", nullable = false)
  private PropertyType propertyType;

  @Column(name = "listing_price", nullable = false)
  private Double listingPrice;

  @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
  @JsonManagedReference
  private List<OfferEntity> offers;
}
