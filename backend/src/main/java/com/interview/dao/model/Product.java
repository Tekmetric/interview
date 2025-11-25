package com.interview.dao.model;

import com.interview.util.Money;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String description;

	@Convert(converter = Money.JpaConverter.class)
	private Money price;

	@CreationTimestamp
	@Column(updatable = false)
	private Instant createdDate;

	@UpdateTimestamp
	private Instant modifiedDate;
	private Instant deletedDate;
	@Version
	private Long version;
}
