package com.interview.autoshop.services.impl;

import com.interview.autoshop.dto.ClientDto;
import com.interview.autoshop.dto.create.CreateClientDto;
import com.interview.autoshop.exceptions.ClientNotFoundException;
import com.interview.autoshop.model.Client;
import com.interview.autoshop.repositories.ClientRepository;
import com.interview.autoshop.services.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    ClientDto entityToDto(Client client){
        return ClientDto.builder()
                .id(client.getId())
                .name(client.getName())
                .address(client.getAddress())
                .phone(client.getPhone())
                .email(client.getEmail())
                .build();
    }

    Client createDtoToEntity(CreateClientDto clientDto){
        return Client.builder()
                .name(clientDto.getName())
                .address(clientDto.getAddress())
                .phone(clientDto.getPhone())
                .email(clientDto.getEmail())
                .build();
    }

    @Override
    public Optional<ClientDto> findById(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        return client.map(this::entityToDto);
    }

    @Override
    public Optional<Client> getEntityById(Long id) {
        return clientRepository.findById(id);
    }

    @Override
    public List<ClientDto> list(String email, Pageable p) {
        Page<Client> clients = clientRepository.findByEmailStartsWithIgnoreCase(email, p);
        return clients.stream().map(this::entityToDto).collect(Collectors.toList());
    }

    @Override
    public ClientDto create(CreateClientDto clientDto) {
        Client client = createDtoToEntity(clientDto);
        client.setEmail(client.getEmail().toLowerCase());
        Client savedClient = clientRepository.save(client);
        return entityToDto(savedClient);
    }

    @Override
    public ClientDto update(Long id, CreateClientDto clientDto) {
        if(isClientPresent(id)) {
            Client client = createDtoToEntity(clientDto);
            client.setEmail(client.getEmail().toLowerCase());
            client.setId(id);
            Client savedClient = clientRepository.save(client);
            return entityToDto(savedClient);
        }
        throw new ClientNotFoundException();
    }


    @Override
    public void delete(Long id) {
        if(isClientPresent(id)){
            clientRepository.deleteById(id);
        }
        else{
            log.debug("Client with id not found : " + id);
        }
    }

    @Override
    public boolean isClientPresent(Long id) {
        return clientRepository.existsById(id);
    }
}
