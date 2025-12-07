package com.tekmetric.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User, UUID> {}
