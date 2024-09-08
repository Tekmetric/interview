package com.interview.autoshop.services;

import com.interview.autoshop.dto.ClientDto;
import com.interview.autoshop.dto.create.CreateClientDto;
import com.interview.autoshop.model.Client;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ClientService {

    Optional<ClientDto> findById(Long id);

    Optional<Client> getEntityById(Long id);

    List<ClientDto> list(String email, Pageable p);

    ClientDto create(CreateClientDto carDto);

    ClientDto update(Long id, CreateClientDto carDto);

    void delete(Long id);

    boolean isClientPresent(Long id);
}
