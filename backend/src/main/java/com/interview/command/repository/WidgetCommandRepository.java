package com.interview.command.repository;

import com.interview.common.entity.Widget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WidgetCommandRepository extends JpaRepository<Widget, Long> {
}
