package com.interview.controller;

import com.interview.dto.PersonDTO;
import com.interview.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/person")
@RequiredArgsConstructor
@Slf4j
public class PersonRestController extends AbstractController {

    private final PersonService personService;

    @GetMapping("/{id}/get")
    public ResponseEntity<APIResponse> getPersonById(@PathVariable int id) {

        APIResponse response = new APIResponse();

        try {

            Optional<PersonDTO> personDTOOpt = personService.findPersonById(id);

            if (personDTOOpt.isPresent()) {

                response.setData(personDTOOpt.get());

                return ResponseEntity.ok(response);

            } else {

                response.setMessage("Person not found.");

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

            }

        } catch (Exception e) {

            return getErrorApiResponseEntity("Error in getPersonById: " + id, e, response);

        }

    }

    @GetMapping("/list")
    public ResponseEntity<APIResponse> getPersons(@RequestParam(required = false, defaultValue = "20") int size,
                                                  @RequestParam(required = false, defaultValue = "0") int page,
                                                  @RequestParam(required = false, defaultValue = "id") String sort) {

        APIResponse response = new APIResponse();

        try {

            Pagination pagination = new Pagination(page >= 0 ? page : 0, size > 0 ? size : 20, sort);

            response.setData(personService.findAllPersons(pagination));

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            return getErrorApiResponseEntity("Error in getAllPersons", e, response);

        }

    }

    @PostMapping("/create")
    public ResponseEntity<APIResponse> createPerson(@RequestBody PersonDTO personDTO) {

        APIResponse response = new APIResponse();

        try {

            response.setData(personService.savePerson(personDTO));

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            return getErrorApiResponseEntity("Error in createPerson: " + personDTO.getEmail(), e, response);

        }

    }

    @PutMapping("/update")
    public ResponseEntity<APIResponse> updatePerson(@RequestBody PersonDTO personDTO) {

        APIResponse response = new APIResponse();

        try {

            response.setData(personService.savePerson(personDTO));

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            return getErrorApiResponseEntity("Error in updatePerson: " + personDTO.getEmail(), e, response);

        }

    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<APIResponse> deletePerson(@PathVariable int id) {

        APIResponse response = new APIResponse();

        try {

            personService.deletePersonById(id);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            return getErrorApiResponseEntity("Error in deletePerson: " + id, e, response);

        }

    }

}
