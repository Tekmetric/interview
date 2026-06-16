package com.interview.adapter.in.web;

import com.interview.api.AutoPartsServiceApi;
import com.interview.application.port.in.AutoPartUseCase;
import com.interview.common.exception.AutoPartServiceException;
import com.interview.model.AutoPart;
import com.interview.model.AutoPartResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AutoPartController implements AutoPartsServiceApi {
    /*
     * Go to 'http://localhost:8080/swagger-ui.html' to see the OpenAPI generated documentation for this controller
     */

    private final AutoPartUseCase autoPartUseCase;

    @Autowired
    public AutoPartController(AutoPartUseCase autoPartUseCase) {
        this.autoPartUseCase = autoPartUseCase;
    }

    @Override
    public ResponseEntity<AutoPartResponse> createAutoPart(AutoPart autoPart) {
        try {
            AutoPartResponse response = autoPartUseCase.createAutoPart(autoPart);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (AutoPartServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new AutoPartServiceException("An unexpected error occurred while creating the auto part.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<AutoPartResponse> retrieveAutoParts(String id) {
        AutoPartResponse response;
        try {
            if (id == null || id.isBlank()) {
                response = autoPartUseCase.retrieveAllAutoParts();
            } else {
                response = autoPartUseCase.retrieveSingleAutoPart(id);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (AutoPartServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new AutoPartServiceException("An unexpected error occurred while retrieving the auto part(s).", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * NOTE: This PUT method could probably be a PATCH method following the RFC 5789 standard.
     * I went with a PUT for the sake of mapping simplicity.
     */
    @Override
    public ResponseEntity<AutoPartResponse> updateAutoPart(String id, AutoPart autoPart) {
        try {
            AutoPartResponse response = autoPartUseCase.updateAutoPart(id, autoPart);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (AutoPartServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new AutoPartServiceException("An unexpected error occurred while updating the auto part.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> deleteAutoPart(String id) {
        try {
            autoPartUseCase.deleteAutoPart(id);
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Part with ID '" + id + "' has been deleted successfully");
        } catch (AutoPartServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new AutoPartServiceException("An unexpected error occurred while deleting the auto part.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
