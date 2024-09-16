package com.interview.repository.specification;


import com.interview.domain.Artist;
import com.interview.domain.VinylRecord;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class VinylRecordSpecification {

    public static Specification<VinylRecord> byTitle(String title) {
        return ((root, query, criteriaBuilder) -> {
            if (StringUtils.isNotBlank(title)) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
            }
            return criteriaBuilder.conjunction();
        });
    }

    public static Specification<VinylRecord> byArtist(String artistQueryString) {
        return ((root, query, criteriaBuilder) -> {
            if (artistQueryString != null) {
                Join<VinylRecord, Artist> artist = root.join("artists", JoinType.LEFT);
                return criteriaBuilder.like(criteriaBuilder.lower(artist.get("name")), "%" + artistQueryString.toLowerCase() + "%");
            }
            return criteriaBuilder.conjunction();
        });
    }

}
