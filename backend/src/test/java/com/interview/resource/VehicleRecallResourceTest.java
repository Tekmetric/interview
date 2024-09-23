package com.interview.resource;

import com.interview.exception.ValidationException;
import com.interview.model.VehicleRecall;
import com.interview.service.VehicleRecallService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class VehicleRecallResourceTest {

    @Mock
    private VehicleRecallService vehicleRecallService;

    @InjectMocks
    private VehicleRecallResource vehicleRecallResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllRecallsShouldReturnListOfRecalls() {
        List<VehicleRecall> expectedRecalls = Arrays.asList(
                new VehicleRecall("Make1", "Model1", 2020, "Recall1", LocalDate.of(2021, 12, 31)),
                new VehicleRecall("Make2", "Model2", 2021, "Recall2", LocalDate.of(2022, 12, 31))
        );
        when(vehicleRecallService.getAllRecalls()).thenReturn(expectedRecalls);

        ResponseEntity<List<VehicleRecall>> response = vehicleRecallResource.getAllRecalls();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedRecalls, response.getBody());
    }

    @Test
    void getRecallByIdExistingIdShouldReturnRecall() {
        VehicleRecall expectedRecall = new VehicleRecall("Make", "Model", 2020, "Recall", LocalDate.of(2021, 12, 31));
        when(vehicleRecallService.getRecallById(any())).thenReturn(Optional.of(expectedRecall));

        ResponseEntity<VehicleRecall> response = vehicleRecallResource.getRecallById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedRecall, response.getBody());
    }

    @Test
    void getRecallByIdNonExistingIdShouldReturnNotFound() {
        when(vehicleRecallService.getRecallById(any())).thenReturn(Optional.empty());

        ResponseEntity<VehicleRecall> response = vehicleRecallResource.getRecallById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createRecallValidRecallShouldReturnCreatedRecall() {
        VehicleRecall inputRecall = new VehicleRecall("Make", "Model", 2020, "Recall", LocalDate.of(2021, 12, 31));
        VehicleRecall createdRecall = new VehicleRecall("Make", "Model", 2020, "Recall", LocalDate.of(2021, 12, 31));
        when(vehicleRecallService.createRecall(inputRecall)).thenReturn(createdRecall);

        ResponseEntity<?> response = vehicleRecallResource.createRecall(inputRecall);
        assertNotNull(response.getBody());
        assertInstanceOf(VehicleRecall.class, response.getBody());
        VehicleRecall responseRecall = (VehicleRecall) response.getBody();
        assertEquals(inputRecall.getMake(), responseRecall.getMake());
        assertEquals(inputRecall.getModel(), responseRecall.getModel());
        assertEquals(inputRecall.getModelYear(), responseRecall.getModelYear());
        assertEquals(inputRecall.getRecallDescription(), responseRecall.getRecallDescription());
    }

    @Test
    void createRecallInvalidRecallShouldReturnBadRequest() {
        VehicleRecall invalidRecall = new VehicleRecall("", "", 0, "", LocalDate.of(1970, 1, 1));
        when(vehicleRecallService.createRecall(invalidRecall))
                .thenThrow(new ValidationException(Arrays.asList("Error1", "Error2")));

        ResponseEntity<?> response = vehicleRecallResource.createRecall(invalidRecall);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(List.class, response.getBody());
        assertEquals(2, ((List<?>) response.getBody()).size());
    }

    @Test
    void updateRecallExistingIdAndValidRecallShouldReturnUpdatedRecall() {
        VehicleRecall inputRecall = new VehicleRecall("UpdatedMake", "UpdatedModel", 2021, "UpdatedRecall", LocalDate.of(2023, 12, 31));
        VehicleRecall updatedRecall = new VehicleRecall("UpdatedMake", "UpdatedModel", 2021, "UpdatedRecall", LocalDate.of(2023, 12, 31));
        when(vehicleRecallService.updateRecall(any(), eq(inputRecall))).thenReturn(Optional.of(updatedRecall));

        ResponseEntity<?> response = vehicleRecallResource.updateRecall(1L, inputRecall);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(VehicleRecall.class, response.getBody());
        VehicleRecall responseRecall = (VehicleRecall) response.getBody();
        assertEquals(inputRecall.getMake(), responseRecall.getMake());
        assertEquals(inputRecall.getModel(), responseRecall.getModel());
        assertEquals(inputRecall.getModelYear(), responseRecall.getModelYear());
        assertEquals(inputRecall.getRecallDescription(), responseRecall.getRecallDescription());
    }

    @Test
    void updateRecallNonExistingIdShouldReturnNotFound() {
        VehicleRecall inputRecall = new VehicleRecall("Make", "Model", 2020, "Recall", LocalDate.of(2023, 12, 31));
        when(vehicleRecallService.updateRecall(any(), eq(inputRecall))).thenReturn(Optional.empty());

        ResponseEntity<?> response = vehicleRecallResource.updateRecall(1L, inputRecall);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateRecall_invalidRecall_shouldReturnBadRequest() {
        VehicleRecall invalidRecall = new VehicleRecall("", "", 0, "", LocalDate.of(1970, 1, 1));
        when(vehicleRecallService.updateRecall(any(), eq(invalidRecall)))
                .thenThrow(new ValidationException(Arrays.asList("Error1", "Error2")));

        ResponseEntity<?> response = vehicleRecallResource.updateRecall(1L, invalidRecall);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(List.class, response.getBody());
        assertEquals(2, ((List<?>) response.getBody()).size());
    }

    @Test
    void deleteRecall_existingId_shouldReturnNoContent() {
        when(vehicleRecallService.deleteRecall(any())).thenReturn(true);

        ResponseEntity<Void> response = vehicleRecallResource.deleteRecall(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteRecall_nonExistingId_shouldReturnNotFound() {
        when(vehicleRecallService.deleteRecall(any())).thenReturn(false);

        ResponseEntity<Void> response = vehicleRecallResource.deleteRecall(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
