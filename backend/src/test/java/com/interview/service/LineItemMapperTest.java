package com.interview.service;

import static org.assertj.core.api.BDDAssertions.then;

import com.interview.dto.CreateLineItemCommand;
import com.interview.model.LineItem;
import java.math.BigDecimal;
import java.util.List;
import org.assertj.core.api.BDDSoftAssertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

@DisplayName("LineItemMapper")
class LineItemMapperTest {

  private final LineItemMapper mapper = Mappers.getMapper(LineItemMapper.class);

  @Nested
  @DisplayName("toDto")
  class ToDto {

    @Test
    @DisplayName("given line item, when mapping to dto, then maps all fields")
    void givenLineItem_whenMappingToDto_thenMapsAllFields() {
      // Given
      var lineItem = Instancio.create(LineItem.class);

      // When
      var result = mapper.toDto(lineItem);

      // Then
      BDDSoftAssertions.thenSoftly(softly -> {
        softly.then(result.id()).isEqualTo(lineItem.getId());
        softly.then(result.description()).isEqualTo(lineItem.getDescription());
        softly.then(result.unitPrice())
            .isEqualByComparingTo(lineItem.getUnitPrice());
        softly.then(result.version()).isEqualTo(lineItem.getVersion());
        softly.then(result.createdAt()).isEqualTo(lineItem.getCreatedAt());
        softly.then(result.updatedAt()).isEqualTo(lineItem.getUpdatedAt());
      });
    }

    @Test
    @DisplayName("given null line item, when mapping to dto, then returns null")
    void givenNullLineItem_whenMappingToDto_thenReturnsNull() {
      // When
      var result = mapper.toDto(null);

      // Then
      then(result).isNull();
    }
  }

  @Nested
  @DisplayName("toDtos")
  class ToDtos {

    @Test
    @DisplayName("given list of line items, when mapping to dtos, "
        + "then maps all elements")
    void givenListOfLineItems_whenMappingToDtos_thenMapsAllElements() {
      // Given
      var lineItems = Instancio.ofList(LineItem.class).size(3).create();

      // When
      var result = mapper.toDtos(lineItems);

      // Then
      BDDSoftAssertions.thenSoftly(softly -> {
        softly.then(result).hasSize(3);
        for (int i = 0; i < lineItems.size(); i++) {
          softly.then(result.get(i).id())
              .isEqualTo(lineItems.get(i).getId());
        }
      });
    }

    @Test
    @DisplayName("given empty list, when mapping to dtos, then returns empty list")
    void givenEmptyList_whenMappingToDtos_thenReturnsEmptyList() {
      // When
      var result = mapper.toDtos(List.of());

      // Then
      then(result).isEmpty();
    }

    @Test
    @DisplayName("given null list, when mapping to dtos, then returns null")
    void givenNullList_whenMappingToDtos_thenReturnsNull() {
      // When
      var result = mapper.toDtos(null);

      // Then
      then(result).isNull();
    }
  }

  @Nested
  @DisplayName("toEntity")
  class ToEntity {

    @Test
    @DisplayName("given command, when mapping to entity, then maps fields and ignores managed fields")
    void givenCommand_whenMappingToEntity_thenMapsFieldsAndIgnoresManagedFields() {
      // Given
      var command = new CreateLineItemCommand("Oil filter", BigDecimal.valueOf(12.50));

      // When
      var result = mapper.toEntity(command);

      // Then
      BDDSoftAssertions.thenSoftly(softly -> {
        softly.then(result.getDescription()).isEqualTo("Oil filter");
        softly.then(result.getUnitPrice()).isEqualByComparingTo(BigDecimal.valueOf(12.50));
        softly.then(result.getId()).isNull();
        softly.then(result.getRepairOrder()).isNull();
        softly.then(result.getVersion()).isNull();
        softly.then(result.getCreatedAt()).isNull();
        softly.then(result.getUpdatedAt()).isNull();
      });
    }

    @Test
    @DisplayName("given null command, when mapping to entity, then returns null")
    void givenNullCommand_whenMappingToEntity_thenReturnsNull() {
      // When
      var result = mapper.toEntity(null);

      // Then
      then(result).isNull();
    }
  }
}
