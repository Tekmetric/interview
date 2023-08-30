package com.interview.util;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecificationUtil<T> {
    /**
     * Parse query to the criteria object (key, operation, value)
     *
     * @param query - provided query that need to be parsed
     * @return a list of search criteria
     */
    public static List<SearchCriteria> getSearchCriteria(String query) {

        Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(.*?),");
        Matcher matcher = pattern.matcher(query + ",");
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        while (matcher.find()) {
            searchCriteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
        }

        return searchCriteriaList;
    }

    public static <T> Optional<Specification<T>> buildSpecifications(List<Specification<T>> specs) {
        if (CollectionUtils.isEmpty(specs)) {
            return Optional.empty();
        }
        Specification<T> specFilter = Specification.where(specs.get(0));
        for (int i = 1; i < specs.size(); i++) {
            specFilter = specFilter.and(specs.get(i));
        }

        return Optional.of(specFilter);
    }
}
