package com.interview.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.interview.model.Popstar;

public interface PopstarRepository extends JpaRepository <Popstar, Long> {
}