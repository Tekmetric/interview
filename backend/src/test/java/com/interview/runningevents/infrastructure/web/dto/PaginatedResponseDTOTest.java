package com.interview.runningevents.infrastructure.web.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PaginatedResponseDTOTest {

    @Test
    public void shouldCreateValidDTO() {
        // Given
        List<String> items = Arrays.asList("Item 1", "Item 2", "Item 3");

        PaginatedResponseDTO<String> dto = PaginatedResponseDTO.<String>builder()
                .items(items)
                .totalItems(10)
                .page(1)
                .pageSize(3)
                .totalPages(4)
                .hasPrevious(true)
                .hasNext(true)
                .build();

        // Then
        assertThat(dto.getItems()).hasSize(3);
        assertThat(dto.getItems()).containsExactly("Item 1", "Item 2", "Item 3");
        assertThat(dto.getTotalItems()).isEqualTo(10);
        assertThat(dto.getPage()).isEqualTo(1);
        assertThat(dto.getPageSize()).isEqualTo(3);
        assertThat(dto.getTotalPages()).isEqualTo(4);
        assertThat(dto.isHasPrevious()).isTrue();
        assertThat(dto.isHasNext()).isTrue();
    }

    @Test
    public void shouldWorkWithEmptyItems() {
        // Given
        PaginatedResponseDTO<String> dto = PaginatedResponseDTO.<String>builder()
                .items(Arrays.asList())
                .totalItems(0)
                .page(0)
                .pageSize(10)
                .totalPages(0)
                .hasPrevious(false)
                .hasNext(false)
                .build();

        // Then
        assertThat(dto.getItems()).isEmpty();
        assertThat(dto.getTotalItems()).isEqualTo(0);
        assertThat(dto.getPage()).isEqualTo(0);
        assertThat(dto.isHasPrevious()).isFalse();
        assertThat(dto.isHasNext()).isFalse();
    }

    @Test
    public void shouldSupportNoArgsConstructor() {
        // Given
        PaginatedResponseDTO<Object> dto = new PaginatedResponseDTO<>();

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getItems()).isNull();
        assertThat(dto.getTotalItems()).isEqualTo(0);
        assertThat(dto.getPage()).isEqualTo(0);
    }

    @Test
    public void shouldSupportSetters() {
        // Given
        PaginatedResponseDTO<String> dto = new PaginatedResponseDTO<>();
        List<String> items = Arrays.asList("Item 1", "Item 2");

        // When
        dto.setItems(items);
        dto.setTotalItems(20);
        dto.setPage(2);
        dto.setPageSize(5);
        dto.setTotalPages(4);
        dto.setHasPrevious(true);
        dto.setHasNext(true);

        // Then
        assertThat(dto.getItems()).containsExactly("Item 1", "Item 2");
        assertThat(dto.getTotalItems()).isEqualTo(20);
        assertThat(dto.getPage()).isEqualTo(2);
        assertThat(dto.getPageSize()).isEqualTo(5);
        assertThat(dto.getTotalPages()).isEqualTo(4);
        assertThat(dto.isHasPrevious()).isTrue();
        assertThat(dto.isHasNext()).isTrue();
    }

    @Test
    public void shouldSupportGenericTypes() {
        // Test with Integer items
        PaginatedResponseDTO<Integer> intDto = PaginatedResponseDTO.<Integer>builder()
                .items(Arrays.asList(1, 2, 3))
                .totalItems(10)
                .build();

        assertThat(intDto.getItems()).containsExactly(1, 2, 3);

        // Test with RunningEventResponseDTO items
        RunningEventResponseDTO eventDto =
                RunningEventResponseDTO.builder().id(1L).name("Test Event").build();

        PaginatedResponseDTO<RunningEventResponseDTO> eventListDto =
                PaginatedResponseDTO.<RunningEventResponseDTO>builder()
                        .items(Arrays.asList(eventDto))
                        .totalItems(1)
                        .build();

        assertThat(eventListDto.getItems()).hasSize(1);
        assertThat(eventListDto.getItems().get(0).getId()).isEqualTo(1L);
        assertThat(eventListDto.getItems().get(0).getName()).isEqualTo("Test Event");
    }
}
