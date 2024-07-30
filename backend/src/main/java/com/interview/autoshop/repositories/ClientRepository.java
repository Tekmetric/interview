package com.interview.autoshop.repositories;

import com.interview.autoshop.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    public Page<Client> findByEmailStartsWithIgnoreCase(final String email, Pageable p);
}
