package com.interview.mapper;

import com.interview.dto.search.Direction;
import com.interview.dto.search.FieldName;
import com.interview.dto.search.PageRequestDto;
import com.interview.dto.search.PageResponseDto;
import com.interview.dto.search.SortBy;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class PaginationMapperTest {

    @Test
    public void toPageRequest_NoSortBy_PageRequestWithoutSort() {
        // Given
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, null);

        // When
        PageRequest result = PaginationMapper.toPageRequest(pageRequestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(10);
        assertThat(result.getSort()).isEqualTo(Sort.unsorted());
    }

    @Test
    public void toPageRequest_EmptySortBy_PageRequestWithoutSort() {
        // Given
        PageRequestDto pageRequestDto = new PageRequestDto(1, 20, List.of());

        // When
        PageRequest result = PaginationMapper.toPageRequest(pageRequestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPageNumber()).isEqualTo(1);
        assertThat(result.getPageSize()).isEqualTo(20);
        assertThat(result.getSort()).isEqualTo(Sort.unsorted());
    }

    @Test
    public void toPageRequest_SingleSortByAsc_PageRequestWithAscSort() {
        // Given
        SortBy sortBy = new SortBy(FieldName.TYPE, Direction.ASC);
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, List.of(sortBy));

        // When
        PageRequest result = PaginationMapper.toPageRequest(pageRequestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(10);
        assertThat(result.getSort().isSorted()).isTrue();
        assertThat(result.getSort().getOrderFor("type")).isNotNull();
        assertThat(result.getSort().getOrderFor("type").getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    public void toPageRequest_SingleSortByDesc_PageRequestWithDescSort() {
        // Given
        SortBy sortBy = new SortBy(FieldName.PRODUCTION_YEAR, Direction.DESC);
        PageRequestDto pageRequestDto = new PageRequestDto(2, 15, List.of(sortBy));

        // When
        PageRequest result = PaginationMapper.toPageRequest(pageRequestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPageNumber()).isEqualTo(2);
        assertThat(result.getPageSize()).isEqualTo(15);
        assertThat(result.getSort().isSorted()).isTrue();
        assertThat(result.getSort().getOrderFor("productionYear")).isNotNull();
        assertThat(result.getSort().getOrderFor("productionYear").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    public void toPageRequest_MultipleSortBy_PageRequestWithMultipleSort() {
        // Given
        List<SortBy> sortByList = List.of(
                new SortBy(FieldName.TYPE, Direction.ASC),
                new SortBy(FieldName.CREATED_AT, Direction.DESC)
        );
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, sortByList);

        // When
        PageRequest result = PaginationMapper.toPageRequest(pageRequestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSort().isSorted()).isTrue();
        assertThat(result.getSort().getOrderFor("type")).isNotNull();
        assertThat(result.getSort().getOrderFor("type").getDirection()).isEqualTo(Sort.Direction.ASC);
        assertThat(result.getSort().getOrderFor("createdAt")).isNotNull();
        assertThat(result.getSort().getOrderFor("createdAt").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    public void toPageResponse_EmptyPage_EmptyPageResponse() {
        // Given
        Page<String> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        Function<String, Integer> mappingFunction = String::length;

        // When
        PageResponseDto<Integer> result = PaginationMapper.toPageResponse(emptyPage, mappingFunction);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.pageNumber()).isEqualTo(0);
        assertThat(result.pageSize()).isEqualTo(0);
        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0);
        assertThat(result.totalPages()).isEqualTo(0);
    }

    @Test
    public void toPageResponse_PageWithContent_MappedPageResponse() {
        // Given
        List<String> content = List.of("hello", "world", "test");
        Page<String> page = new PageImpl<>(content, PageRequest.of(1, 3), 10);
        Function<String, Integer> mappingFunction = String::length;

        // When
        PageResponseDto<Integer> result = PaginationMapper.toPageResponse(page, mappingFunction);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.pageNumber()).isEqualTo(1);
        assertThat(result.pageSize()).isEqualTo(3);
        assertThat(result.content()).containsExactly(5, 5, 4);
        assertThat(result.totalElements()).isEqualTo(10);
        assertThat(result.totalPages()).isEqualTo(4);
    }

    @Test
    public void toPageResponse_ComplexMapping_MappedPageResponse() {
        // Given
        List<TestEntity> entities = List.of(
                new TestEntity("John", 25),
                new TestEntity("Jane", 30)
        );
        Page<TestEntity> page = new PageImpl<>(entities, PageRequest.of(0, 2), 2);
        Function<TestEntity, TestDto> mappingFunction = entity -> 
                new TestDto(entity.name().toUpperCase(), entity.age() * 2);

        // When
        PageResponseDto<TestDto> result = PaginationMapper.toPageResponse(page, mappingFunction);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(2);
        assertThat(result.content().get(0).name()).isEqualTo("JOHN");
        assertThat(result.content().get(0).age()).isEqualTo(50);
        assertThat(result.content().get(1).name()).isEqualTo("JANE");
        assertThat(result.content().get(1).age()).isEqualTo(60);
    }

    private record TestEntity(String name, int age) {}
    private record TestDto(String name, int age) {}

}
