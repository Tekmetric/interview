package com.interview.query.repository;

import com.interview.common.entity.Widget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WidgetQueryRepository extends JpaRepository<Widget, Long> {
}
