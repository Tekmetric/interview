package com.interview.repository;

import com.interview.dto.JobPostingFilter;
import com.interview.model.JobPosting;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * Factory of composable, null-safe {@link Specification} predicates for {@link JobPosting}.
 *
 * <p>Every predicate returns {@code null} when its criterion is absent, which
 * {@code Specification.where(...).and(...)} treats as "no restriction" — so the
 * final SQL only gains a WHERE clause for filters the caller actually supplied.
 */
@UtilityClass
public class JobPostingSpecification {

    /**
     * Build a combined spec from all non-null fields of the filter object.
     */
    public static Specification<JobPosting> fromFilter(JobPostingFilter filter) {
        return Specification.where(isRemote(filter.remote())).and(locationContains(filter.location())).and(titleContains(filter.titleContains()));
    }

    public static Specification<JobPosting> isRemote(Boolean remote) {
        return (root, query, cb) -> remote == null ? null : cb.equal(root.get("remote"), remote);
    }

    public static Specification<JobPosting> locationContains(String location) {
        return (root, query, cb) -> StringUtils.hasText(location) ? cb.like(cb.lower(root.get("location")), "%" + location.trim().toLowerCase() + "%") : null;
    }

    public static Specification<JobPosting> titleContains(String title) {
        return (root, query, cb) -> StringUtils.hasText(title) ? cb.like(cb.lower(root.get("title")), "%" + title.trim().toLowerCase() + "%") : null;
    }
}
