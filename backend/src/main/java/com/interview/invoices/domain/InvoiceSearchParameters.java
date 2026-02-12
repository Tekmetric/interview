package com.interview.invoices.domain;

import com.interview.invoices.domain.CustomerInvoice.Status;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import org.immutables.value.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

@Value.Immutable
public abstract class InvoiceSearchParameters {

  public abstract Optional<String> invoiceNumber();

  public abstract Optional<String> customerId();

  public abstract List<Status> statuses();

  public abstract Optional<LocalDate> issueDateFrom();

  public abstract Optional<LocalDate> issueDateTo();

  public abstract Optional<LocalDate> dueDateFrom();

  public abstract Optional<LocalDate> dueDateTo();

  public abstract Optional<LocalDate> paidDateFrom();

  public abstract Optional<LocalDate> paidDateTo();

  public abstract int pageNumber();

  public abstract int pageSize();

  public abstract SortField sortBy();

  public abstract SortOrder orderBy();

  @Value.Derived
  public Specification<CustomerInvoice> repositorySpecification() {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      invoiceNumber().ifPresent(num -> {
        Expression<String> lowerCaseInvoiceNumberDB = criteriaBuilder.lower(root.get("invoiceNumber"));
        String lowerCaseSearchTerm = "%" + num.toLowerCase() + "%";
        predicates.add(criteriaBuilder.like(lowerCaseInvoiceNumberDB, lowerCaseSearchTerm));
      });
      customerId().ifPresent(id ->
          predicates.add(criteriaBuilder.equal(root.get("customerId"), id))
      );

      if (!statuses().isEmpty()) {
        predicates.add(root.get("status").in(statuses()));
      }

      issueDateFrom().ifPresent(date ->
          predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("issueDate"), date))
      );
      issueDateTo().ifPresent(date ->
          predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("issueDate"), date))
      );

      dueDateFrom().ifPresent(date ->
          predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate"), date))
      );
      dueDateTo().ifPresent(date ->
          predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dueDate"), date))
      );

      paidDateFrom().ifPresent(date ->
          predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("paidDate"), date))
      );
      paidDateTo().ifPresent(date ->
          predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("paidDate"), date))
      );

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  @Value.Derived
  public Pageable pageableRequest() {
    String sortFieldName = sortBy().name();
    Sort sort = Sort.by(orderBy().getSpringDirection(), sortFieldName);
    return PageRequest.of(pageNumber(), pageSize(), sort);
  }

  @Value.Check
  public void check() {
    Assert.isTrue(pageNumber() >= 0, "'pageNumber' should not be negative");
    Assert.isTrue(pageSize() > 0 && pageSize() <= 200, "'pageSize' must be between 1 and 200");
    checkDateRange(issueDateFrom(), issueDateTo(), "issueDate");
    checkDateRange(dueDateFrom(), dueDateTo(), "dueDate");
    checkDateRange(paidDateFrom(), paidDateTo(), "paidDate");
  }

  private static void checkDateRange(Optional<LocalDate> from, Optional<LocalDate> to, String field) {
    if (from.isPresent() && to.isPresent()) {
      Assert.isTrue(!from.get().isAfter(to.get()), String.format("'%sFrom' must be before or equal to '%sTo'", field, field));
    }
  }

  public enum SortField {
    invoiceNumber, status, customerId, issueDate, dueDate, paidDate
  }

  public enum SortOrder {
    asc(Sort.Direction.ASC), desc(Sort.Direction.DESC);

    private final Sort.Direction direction;

    SortOrder(Direction direction) {
      this.direction = direction;
    }

    private Sort.Direction getSpringDirection() {
      return direction;
    }
  }
}
