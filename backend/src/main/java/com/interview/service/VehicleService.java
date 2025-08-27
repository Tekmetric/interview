package com.interview.service;

import com.interview.domain.Vehicle;
import com.interview.dto.UpsertVehicleDto;
import com.interview.dto.VehicleDto;
import com.interview.dto.search.PageResponseDto;
import com.interview.dto.search.VehicleSearchCriteriaDto;
import com.interview.dto.search.VehicleSearchDto;
import com.interview.exception.DuplicateException;
import com.interview.exception.ErrorCode;
import com.interview.exception.NotFoundException;
import com.interview.filter.VehicleQueryFilter;
import com.interview.mapper.VehicleMapper;
import com.interview.repository.VehicleRepository;
import com.interview.repository.VehicleSpecificationBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Set;

import static com.interview.mapper.PaginationMapper.toPageRequest;
import static com.interview.mapper.PaginationMapper.toPageResponse;
import static com.interview.mapper.VehicleMapper.toDto;
import static com.interview.mapper.VehicleMapper.toEntity;

@RequiredArgsConstructor
@Service
public class VehicleService {

    public static final String VIN_PATTERN_AS_STRING = "^[A-HJ-NPR-Z0-9]{17}$";
    public static final String PRODUCTION_YEAR_PATTERN_AS_STRING = "^(18|19|20)\\d{2}$";

    private final VehicleRepository vehicleRepository;
    private final VehicleSpecificationBuilder vehicleSpecificationBuilder;

    @Transactional
    public VehicleDto getById(final long id) {
        final Vehicle vehicle = getEntityById(id);
        return toDto(vehicle);
    }

    @Transactional
    public VehicleDto create(final UpsertVehicleDto vehicleDto) {
        Specification<Vehicle> vehicleSpecification = vehicleSpecificationBuilder.buildSpecification(VehicleQueryFilter.forVin(vehicleDto.vin()));
        if (vehicleRepository.exists(vehicleSpecification)) {
            throw new DuplicateException(ErrorCode.DUPLICATE_VEHICLE_VIN, "Vehicle with VIN %s already exists".formatted(vehicleDto.vin()));
        }

        Vehicle vehicle = toEntity(vehicleDto);

        final Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return toDto(savedVehicle);
    }

    @Transactional
    public VehicleDto update(final long id, UpsertVehicleDto vehicleDto) {
        final Vehicle vehicle = getEntityById(id);

        if (!vehicleDto.vin().equals(vehicle.getVin())) {
            validateNewVin(id, vehicleDto);
        }

        vehicle.setMake(vehicleDto.make());
        vehicle.setModel(vehicleDto.model());
        vehicle.setVin(vehicleDto.vin());
        vehicle.setProductionYear(Year.parse(vehicleDto.productionYear()));
        vehicle.setType(vehicleDto.type());
        final Vehicle updatedVehicle = vehicleRepository.save(vehicle);

        return toDto(updatedVehicle);
    }

    @Transactional
    public void delete(final long id) {
        final Specification<Vehicle> vehicleSpecification = vehicleSpecificationBuilder.buildSpecification(VehicleQueryFilter.forId(id));
        vehicleRepository.delete(vehicleSpecification);
    }

    public PageResponseDto<VehicleDto> search(final VehicleSearchDto vehicleSearch) {
        final VehicleQueryFilter filter = VehicleMapper.toQueryFilter(vehicleSearch.searchCriteriaDto());
        final Specification<Vehicle> vehicleSpecification = vehicleSpecificationBuilder.buildSpecification(filter);
        final Pageable pageable = toPageRequest(vehicleSearch.pageRequestDto());

        final Page<Vehicle> vehiclePage = vehicleRepository.findAll(vehicleSpecification, pageable);

        return toPageResponse(vehiclePage, VehicleMapper::toDto);
    }

    private Vehicle getEntityById(final long id) {
        final Specification<Vehicle> vehicleSpecification = vehicleSpecificationBuilder.buildSpecification(VehicleQueryFilter.forId(id));
        return vehicleRepository.findOne(vehicleSpecification)
                .orElseThrow(() -> new NotFoundException(ErrorCode.VEHICLE_NOT_FOUND, "Vehicle with id %s not found".formatted(id)));
    }

    private void validateNewVin(final long id, final UpsertVehicleDto vehicleDto) {
        final VehicleQueryFilter filter = VehicleQueryFilter.builder()
                .excludingIds(Set.of(id))
                .includingVins(Set.of(vehicleDto.vin())).build();

        final Specification<Vehicle> vehicleSpecification = vehicleSpecificationBuilder.buildSpecification(filter);
        if (vehicleRepository.exists(vehicleSpecification)) {
            throw new DuplicateException(ErrorCode.DUPLICATE_VEHICLE_VIN, "Vehicle with VIN %s already exists".formatted(vehicleDto.vin()));
        }
    }

}
