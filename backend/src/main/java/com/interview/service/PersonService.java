package com.interview.service;

import com.interview.commands.CreatePersonCommand;
import com.interview.commands.UpdatePersonCommand;
import com.interview.commands.UpsertPersonCommand;
import com.interview.domain.dto.Email;
import com.interview.domain.dto.Person;
import com.interview.domain.entity.PersonEntity;
import com.interview.exceptions.EmailConflictException;
import com.interview.repository.PersonRepository;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Service defining Person CRUD logic.
 */
@Service
public class PersonService {
    @Autowired
    private PersonRepository repository;

    private static final Logger logger = Logger.getLogger(PersonService.class.getName());

    public Optional<Person> findById(UUID id) throws IllegalArgumentException {
        return repository.findById(id).map(Person::fromEntity);
    }

    public Optional<Person> findByEmail(Email email) throws IllegalArgumentException {
        return repository.findByEmail(email.value()).map(Person::fromEntity);
    }

    public Page<Person> findAll(Pageable pageable) throws IllegalArgumentException {
        return repository.findAll(pageable).map(Person::fromEntity);
    }

    public Person create(CreatePersonCommand command) throws IllegalArgumentException {
        try {
            var newPerson = repository.save(PersonEntity.from(command));
            return Person.fromEntity(newPerson);
        } catch (DataIntegrityViolationException dive) {
            throw new EmailConflictException(command.email(), dive);
        }
    }

    public Optional<Person> update(UpdatePersonCommand command) throws IllegalArgumentException {
        logger.info(String.format("model=person, operation=update, status=start, metadata={command=%s}", command));
        var existingPerson =
                repository.findById(command.id())
                        .map(existing -> {
                            logger.info(
                                    String.format(
                                            "model=person, operation=update, status=found_existing, " +
                                                    "metadata={existing=%s}",
                                            existing
                                    )
                            );
                            if (command.firstName() != null) existing.setFirstName(command.firstName());
                            if (command.lastName() != null) existing.setLastName(command.lastName());
                            if (command.phoneNumber() != null) existing.setPhoneNumber(command.phoneNumber());
                            if (command.address() != null) existing.setAddress(command.address());

                            return existing;
                        });
        logger.info(
                String.format("model=person, operation=update, status=success, metadata={updated=%s}", existingPerson)
        );
        return existingPerson.map(Person::fromEntity);
    }

    public Person upsert(UpsertPersonCommand command) throws IllegalArgumentException {
        logger.info(String.format("model=person, operation=upsert, status=start, metadata={command=%s}", command));
        var existingOrNewPerson =
                repository.findByEmail(command.email().value())
                        .map(existing -> {
                            logger.info(
                                    String.format(
                                            "model=person, operation=upsert, status=found_existing, " +
                                                    "metadata={existing=%s}",
                                            existing
                                    )
                            );
                            existing.setFirstName(command.firstName());
                            existing.setLastName(command.lastName());
                            existing.setPhoneNumber(command.phoneNumber());
                            existing.setAddress(command.address());
                            return existing;
                        })
                        .orElseGet(() -> {
                            logger.info("model=person, operation=upsert, status=create_new");
                            return repository.save(PersonEntity.from(command));
                        });

        logger.info(
                String.format(
                        "model=person, operation=upsert, status=success, metadata={updated=%s}",
                        existingOrNewPerson
                )
        );
        return Person.fromEntity(existingOrNewPerson);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
