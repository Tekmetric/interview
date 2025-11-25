package com.interview.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.interview.util.Money;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

/**
 * DTO representation of {@link com.interview.dao.model.Product}.
 *
 * @see com.interview.api.mapper.ProductMapper
 */
@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDto {
	private Long id;
	private String name;
	private String description;

	@JsonDeserialize(using = Money.JsonDeserializer.class)
	private Money price;

	private Instant createdDate;
	private Instant modifiedDate;
	private Instant deletedDate;
	private Long version;
}
