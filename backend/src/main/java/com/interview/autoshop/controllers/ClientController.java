package com.interview.autoshop.controllers;

import com.interview.autoshop.dto.ClientDto;
import com.interview.autoshop.dto.create.CreateClientDto;
import com.interview.autoshop.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService){
        this.clientService = clientService;
    }

    @GetMapping(path = "/api/clients/{id}")
    public ResponseEntity<ClientDto> getClient(@PathVariable Long id){
        Optional<ClientDto> clientDto = clientService.findById(id);
        if(clientDto.isPresent()){
            return new ResponseEntity<ClientDto>(clientDto.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(path = "/api/clients")
    public ResponseEntity<List<ClientDto>> listClients(@RequestParam(defaultValue = "") String email, @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "50") int size){
        Pageable p = PageRequest.of(page, size);
        List<ClientDto> clients = clientService.list(email, p);
        return new ResponseEntity<List<ClientDto>>(clients, HttpStatus.OK);
    }

    @PostMapping(path = "/api/clients")
    public ResponseEntity<ClientDto> createClient(@RequestBody CreateClientDto clientDto){
        ClientDto client = clientService.create(clientDto);
        return new ResponseEntity<ClientDto>(client, HttpStatus.CREATED);
    }

    @PutMapping(path = "/api/clients/{id}")
    public ResponseEntity<ClientDto> updateClient(@PathVariable Long id, @RequestBody CreateClientDto clientDto){
        ClientDto client = clientService.update(id, clientDto);
        return new ResponseEntity<ClientDto>(client, HttpStatus.OK);
    }

    @DeleteMapping(path = "/api/clients/{id}")
    public ResponseEntity deleteClient(@PathVariable Long id){
        clientService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
