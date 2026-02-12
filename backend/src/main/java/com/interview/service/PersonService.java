package com.interview.service;

import com.interview.controller.Pagination;
import com.interview.domain.PersonEntity;
import com.interview.dto.PersonDTO;
import com.interview.repository.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PersonService {

    private final PersonRepository personRepository;

    /**
     * Find a Person by their id
     *
     * @param id
     * @return {@link Optional} {@link PersonDTO} - empty if Not Found
     */
    public Optional<PersonDTO> findPersonById(long id) {

        Optional<PersonEntity> personEntity = personRepository.findById(id);

        if (personEntity.isPresent()) {

            return Optional.of(convertToDTO(personEntity.get()));

        } else {

            return Optional.empty();

        }

    }

    /**
     * Find all persons using {@link Pagination}
     *
     * @param pagination
     * @return List of {@link PersonDTO} never null
     */
    public List<PersonDTO> findAllPersons(Pagination pagination) {

        String sortToUse = "id";

        //sort can only be email or id as they are the only indexes
        if (null != pagination.getSortBy() && pagination.getSortBy().equalsIgnoreCase("email")) {

            sortToUse = "email";

        }

        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize(), Sort.by(sortToUse));

        return personRepository.findAll(pageable).stream().filter(Objects::nonNull).map(this::convertToDTO).toList();

    }

    /**
     * Save a PersonDTO - handles create or update
     *
     * @param personDTO
     * @return {@link PersonDTO}
     */
    @Transactional
    public PersonDTO savePerson(PersonDTO personDTO) {

        PersonEntity personEntity;
        if (personDTO.getId() != null && personDTO.getId() > 0) {
            //if we have an id but didn't find a person then this is an issue
            personEntity = personRepository.findById(personDTO.getId()).orElseThrow();
            //if we have new password one and two then password has been updated
            if (StringUtils.isNotBlank(personDTO.getNewPasswordOne())
                    && StringUtils.isNotBlank(personDTO.getNewPasswordTwo())
                    && StringUtils.isNotBlank(personDTO.getPassword())) {
                //if existing passwords equal and new passwords equal then update password with new value
                if (Objects.equals(personDTO.getPassword(), personEntity.getPassword())
                        && Objects.equals(personDTO.getNewPasswordOne(), personDTO.getNewPasswordTwo())) {
                    //TODO: Password validation (i.e. length, chars etc.)
                    personEntity.setPassword(personDTO.getNewPasswordOne());
                }
            }
        } else {
            personEntity = new PersonEntity();
            //can only set the email on a create
            personEntity.setEmail(personDTO.getEmail());
            personEntity.setPassword(personDTO.getPassword());
        }

        if (Objects.nonNull(personDTO.getName())) {
            personEntity.setName(personDTO.getName());
        }
        personEntity.setState(personDTO.getState());
        personEntity.setNickname(personDTO.getNickname());

        return convertToDTO(personRepository.save(personEntity));

    }

    /**
     * Delete a Person by their id
     *
     * @param id
     */
    public void deletePersonById(long id) {

        personRepository.deleteById(id);

    }

    private PersonDTO convertToDTO(PersonEntity personEntity) {

        PersonDTO personDTO = new PersonDTO();
        personDTO.setId(personEntity.getId());
        personDTO.setName(personEntity.getName());
        personDTO.setEmail(personEntity.getEmail());
        personDTO.setState(personEntity.getState());
        personDTO.setNickname(personEntity.getNickname());
        //never set the password

        return personDTO;

    }

    private PersonEntity convertToEntity(PersonDTO personDTO) {

        PersonEntity personEntity = new PersonEntity();
        personEntity.setId(personDTO.getId());
        personEntity.setName(personDTO.getName());
        personEntity.setEmail(personDTO.getEmail());
        personEntity.setState(personDTO.getState());
        personEntity.setNickname(personDTO.getNickname());
        return personEntity;

    }

}
