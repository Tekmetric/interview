package com.tekmetric.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tekmetric.entity.Car;
import com.tekmetric.model.OwnerFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.Year;
import org.junit.jupiter.api.Test;

class CarQueryTest {

  @SuppressWarnings("unchecked")
  private final Root<Car> root = mock(Root.class);

  private final CriteriaQuery<?> query = mock(CriteriaQuery.class);
  private final CriteriaBuilder cb = mock(CriteriaBuilder.class);

  @Test
  void hasMake_nullMake_returnsNullPredicate() {
    var spec = CarQuery.hasMake(null);

    Predicate result = spec.toPredicate(root, query, cb);

    assertNull(result, "When make is null, predicate should be null so Spring ignores it");
  }

  @Test
  void hasMake_nonNullMake_usesEqualOnMakeField() {
    String make = "Toyota";
    @SuppressWarnings("unchecked")
    Path<Object> makePath = mock(Path.class);
    Predicate expected = mock(Predicate.class);

    when(root.get("make")).thenReturn(makePath);
    when(cb.equal(makePath, make)).thenReturn(expected);

    var spec = CarQuery.hasMake(make);

    Predicate result = spec.toPredicate(root, query, cb);

    assertSame(expected, result);
    verify(root).get("make");
    verify(cb).equal(makePath, make);
  }

  @Test
  void hasModel_nullModel_returnsNullPredicate() {
    var spec = CarQuery.hasModel(null);
    assertNull(spec.toPredicate(root, query, cb));
  }

  @Test
  void hasModel_nonNullModel_usesEqualOnModelField() {
    String model = "RAV4";
    @SuppressWarnings("unchecked")
    Path<Object> modelPath = mock(Path.class);
    Predicate expected = mock(Predicate.class);

    when(root.get("model")).thenReturn(modelPath);
    when(cb.equal(modelPath, model)).thenReturn(expected);

    var spec = CarQuery.hasModel(model);

    Predicate result = spec.toPredicate(root, query, cb);

    assertSame(expected, result);
    verify(root).get("model");
    verify(cb).equal(modelPath, model);
  }

  @Test
  void hasYear_nullYear_returnsNullPredicate() {
    var spec = CarQuery.hasYear(null);
    assertNull(spec.toPredicate(root, query, cb));
  }

  @Test
  void hasYear_nonNullYear_usesEqualOnYearField() {
    Integer year = 2020;
    @SuppressWarnings("unchecked")
    Path<Object> yearPath = mock(Path.class);
    Predicate expected = mock(Predicate.class);

    when(root.get("manufactureYear")).thenReturn(yearPath);
    when(cb.equal(yearPath, Year.of(year))).thenReturn(expected);

    var spec = CarQuery.hasYear(year);

    Predicate result = spec.toPredicate(root, query, cb);

    assertSame(expected, result);
    verify(root).get("manufactureYear");
    verify(cb).equal(yearPath, Year.of(year));
  }

  @Test
  void hasColor_nullColor_returnsNullPredicate() {
    var spec = CarQuery.hasColor(null);
    assertNull(spec.toPredicate(root, query, cb));
  }

  @Test
  void hasColor_nonNullColor_usesEqualOnColorField() {
    String color = "red";
    @SuppressWarnings("unchecked")
    Path<Object> colorPath = mock(Path.class);
    Predicate expected = mock(Predicate.class);

    when(root.get("color")).thenReturn(colorPath);
    when(cb.equal(colorPath, color)).thenReturn(expected);

    var spec = CarQuery.hasColor(color);

    Predicate result = spec.toPredicate(root, query, cb);

    assertSame(expected, result);
    verify(root).get("color");
    verify(cb).equal(colorPath, color);
  }

  @Test
  void hasOwner_nullState_returnsConjunction() {
    Predicate conjunction = mock(Predicate.class);
    when(cb.conjunction()).thenReturn(conjunction);

    var spec = CarQuery.hasOwner(null);

    Predicate result = spec.toPredicate(root, query, cb);

    assertSame(conjunction, result);
    verify(cb).conjunction();
  }

  @Test
  void hasOwner_noOwner_returnsIsNullPredicate() {
    @SuppressWarnings("unchecked")
    Path<Object> ownerPath = mock(Path.class);
    Predicate expected = mock(Predicate.class);

    when(root.get("ownerId")).thenReturn(ownerPath);
    when(cb.isNull(ownerPath)).thenReturn(expected);

    var spec = CarQuery.hasOwner(OwnerFilter.NO_OWNER);

    Predicate result = spec.toPredicate(root, query, cb);

    assertSame(expected, result);
    verify(root).get("ownerId");
    verify(cb).isNull(ownerPath);
  }

  @Test
  void hasOwner_withOwner_returnsIsNotNullPredicate() {
    @SuppressWarnings("unchecked")
    Path<Object> ownerPath = mock(Path.class);
    Predicate expected = mock(Predicate.class);

    when(root.get("ownerId")).thenReturn(ownerPath);
    when(cb.isNotNull(ownerPath)).thenReturn(expected);

    var spec = CarQuery.hasOwner(OwnerFilter.WITH_OWNER);

    Predicate result = spec.toPredicate(root, query, cb);

    assertSame(expected, result);
    verify(root).get("ownerId");
    verify(cb).isNotNull(ownerPath);
  }
}
