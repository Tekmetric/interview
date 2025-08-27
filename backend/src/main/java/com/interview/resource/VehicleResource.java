package com.interview.resource;

import com.interview.dto.UpsertVehicleDto;
import com.interview.dto.VehicleDto;
import com.interview.dto.search.PageResponseDto;
import com.interview.dto.search.VehicleSearchDto;
import com.interview.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vehicles")
public class VehicleResource implements VehicleResourceDocumentation {

    private final VehicleService vehicleService;

    @GetMapping("/{id}")
    public VehicleDto getById(@PathVariable final long id) {
        log.info("Received request to get vehicle with id {}", id);
        final VehicleDto vehicle = vehicleService.getById(id);
        log.info("Successfully retrieved vehicle with id {}", id);

        return  vehicle;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleDto createVehicle(@RequestBody final UpsertVehicleDto upsertVehicleDto) {
        log.info("Received request to create vehicle");
        final VehicleDto vehicle = vehicleService.create(upsertVehicleDto);
        log.info("Successfully created vehicle with id {}", vehicle.id());

        return vehicle;
    }

    @PutMapping("/{id}")
    public VehicleDto updateVehicle(@PathVariable final long id,
                                    @RequestBody final UpsertVehicleDto upsertVehicleDto) {
        log.info("Received request to update vehicle with id {}", id);
        final VehicleDto vehicle = vehicleService.update(id, upsertVehicleDto);
        log.info("Successfully updated vehicle with id {}", vehicle.id());

        return vehicle;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVehicle(@PathVariable final long id) {
        log.info("Received request to delete vehicle with id {}", id);
        vehicleService.delete(id);
        log.info("Successfully deleted vehicle with id {}", id);
    }

    // Given the flexibility of the search criteria, we use a POST method in order to avoid URL length limitations
    @PostMapping("/search")
    public PageResponseDto<VehicleDto> searchVehicles(@RequestBody final VehicleSearchDto vehicleSearch) {
        log.info("Received request to search vehicles");
        final PageResponseDto<VehicleDto> vehiclesSearchResult = vehicleService.search(vehicleSearch);
        log.info("Successfully processed request to search vehicles");

        return  vehiclesSearchResult;
    }

}
