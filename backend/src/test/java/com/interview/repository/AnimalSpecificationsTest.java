package com.interview.repository;

import com.interview.model.Animal;
import com.interview.model.Employee;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class AnimalSpecificationsTest {

    @Mock
    private Root<Animal> root;

    @Mock
    private CriteriaQuery<?> criteriaQuery;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    // Given: Common test data
    private static final String ANIMAL_NAME = "Max";
    private static final LocalDate START_DATE = LocalDate.now().minusYears(5);
    private static final LocalDate END_DATE = LocalDate.now().minusYears(2);
    private static final Long EMPLOYEE_ID = 1L;

    @Test
    void withFilters_ShouldReturnNull_WhenNoFiltersProvided() {
        // Given: No filters
        final Specification<Animal> spec = AnimalSpecifications.withFilters(null, null, null, null);
        
        // When: Creating predicates
        final Predicate result = spec.toPredicate(root, criteriaQuery, criteriaBuilder);

        // Then: Should return null (no filters)
        assertThat(result).isNull();
    }

    @Test
    void withFilters_ShouldCreateNamePredicate_WhenNameProvided() {
        // Given: Name filter
        final Specification<Animal> spec = AnimalSpecifications.withFilters(ANIMAL_NAME, null, null, null);
        final Path<String> namePath = mock(Path.class);
        final Predicate likePredicate = mock(Predicate.class);

        when(root.<String>get("name")).thenReturn(namePath);
        when(criteriaBuilder.lower(namePath)).thenReturn(namePath);
        when(criteriaBuilder.like(namePath, "%" + ANIMAL_NAME.toLowerCase() + "%")).thenReturn(likePredicate);
        when(criteriaBuilder.and(likePredicate)).thenReturn(likePredicate);

        // When: Creating predicates
        final Predicate result = spec.toPredicate(root, criteriaQuery, criteriaBuilder);

        // Then: Should create LIKE predicate
        verify(criteriaBuilder).like(namePath, "%" + ANIMAL_NAME.toLowerCase() + "%");
        assertThat(result).isEqualTo(likePredicate);
    }

    @Test
    void withFilters_ShouldCreateDatePredicates_WhenDatesProvided() {
        // Given: Date range filters
        final Specification<Animal> spec = AnimalSpecifications.withFilters(null, START_DATE, END_DATE, null);
        final Path<LocalDate> datePath = mock(Path.class);
        final Predicate startPredicate = mock(Predicate.class);
        final Predicate endPredicate = mock(Predicate.class);
        final Predicate andPredicate = mock(Predicate.class);

        when(root.<LocalDate>get("dateOfBirth")).thenReturn(datePath);
        when(criteriaBuilder.greaterThanOrEqualTo(datePath, START_DATE)).thenReturn(startPredicate);
        when(criteriaBuilder.lessThanOrEqualTo(datePath, END_DATE)).thenReturn(endPredicate);
        when(criteriaBuilder.and(new Predicate[]{startPredicate, endPredicate})).thenReturn(andPredicate);

        // When: Creating predicates
        final Predicate result = spec.toPredicate(root, criteriaQuery, criteriaBuilder);

        // Then: Should create date range predicates
        verify(criteriaBuilder).greaterThanOrEqualTo(datePath, START_DATE);
        verify(criteriaBuilder).lessThanOrEqualTo(datePath, END_DATE);
        assertThat(result).isEqualTo(andPredicate);
    }

    @Test
    void withFilters_ShouldCreateEmployeePredicate_WhenEmployeeIdProvided() {
        // Given: Employee filter
        final Specification<Animal> spec = AnimalSpecifications.withFilters(null, null, null, EMPLOYEE_ID);
        final Path<Employee> employeePath = mock(Path.class);
        final Path<Long> employeeIdPath = mock(Path.class);
        final Predicate equalPredicate = mock(Predicate.class);

        when(root.<Employee>get("responsibleEmployee")).thenReturn(employeePath);
        when(employeePath.<Long>get("id")).thenReturn(employeeIdPath);
        when(criteriaBuilder.equal(employeeIdPath, EMPLOYEE_ID)).thenReturn(equalPredicate);
        when(criteriaBuilder.and(equalPredicate)).thenReturn(equalPredicate);

        // When: Creating predicates
        final Predicate result = spec.toPredicate(root, criteriaQuery, criteriaBuilder);

        // Then: Should create equality predicate
        verify(criteriaBuilder).equal(employeeIdPath, EMPLOYEE_ID);
        assertThat(result).isEqualTo(equalPredicate);
    }

    @Test
    void withFilters_ShouldCombineAllPredicates_WhenAllFiltersProvided() {
        // Given: All filters
        final Specification<Animal> spec = AnimalSpecifications.withFilters(ANIMAL_NAME, START_DATE, END_DATE, EMPLOYEE_ID);
        final Path<String> namePath = mock(Path.class);
        final Path<LocalDate> datePath = mock(Path.class);
        final Path<Employee> employeePath = mock(Path.class);
        final Path<Long> employeeIdPath = mock(Path.class);
        final Predicate namePredicate = mock(Predicate.class);
        final Predicate startPredicate = mock(Predicate.class);
        final Predicate endPredicate = mock(Predicate.class);
        final Predicate employeePredicate = mock(Predicate.class);
        final Predicate finalPredicate = mock(Predicate.class);

        when(root.<String>get("name")).thenReturn(namePath);
        when(root.<LocalDate>get("dateOfBirth")).thenReturn(datePath);
        when(root.<Employee>get("responsibleEmployee")).thenReturn(employeePath);
        when(employeePath.<Long>get("id")).thenReturn(employeeIdPath);
        when(criteriaBuilder.lower(namePath)).thenReturn(namePath);
        when(criteriaBuilder.like(namePath, "%" + ANIMAL_NAME.toLowerCase() + "%")).thenReturn(namePredicate);
        when(criteriaBuilder.greaterThanOrEqualTo(datePath, START_DATE)).thenReturn(startPredicate);
        when(criteriaBuilder.lessThanOrEqualTo(datePath, END_DATE)).thenReturn(endPredicate);
        when(criteriaBuilder.equal(employeeIdPath, EMPLOYEE_ID)).thenReturn(employeePredicate);
        when(criteriaBuilder.and(namePredicate, startPredicate, endPredicate, employeePredicate)).thenReturn(finalPredicate);

        // When: Creating predicates
        final Predicate result = spec.toPredicate(root, criteriaQuery, criteriaBuilder);

        // Then: Should create and combine all predicates
        verify(criteriaBuilder).like(namePath, "%" + ANIMAL_NAME.toLowerCase() + "%");
        verify(criteriaBuilder).greaterThanOrEqualTo(datePath, START_DATE);
        verify(criteriaBuilder).lessThanOrEqualTo(datePath, END_DATE);
        verify(criteriaBuilder).equal(employeeIdPath, EMPLOYEE_ID);
        assertThat(result).isEqualTo(finalPredicate);
    }

    @Test
    void withFilters_ShouldIgnoreEmptyName_WhenNameIsBlank() {
        // Given: Empty name filter
        final Specification<Animal> spec = AnimalSpecifications.withFilters(" ", null, null, null);
        
        // When: Creating predicates
        final Predicate result = spec.toPredicate(root, criteriaQuery, criteriaBuilder);

        // Then: Should return null (no filters)
        assertThat(result).isNull();
    }
}