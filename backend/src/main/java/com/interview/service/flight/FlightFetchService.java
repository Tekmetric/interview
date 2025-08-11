package com.interview.service.flight;

import com.interview.common.dto.FlightDto;
import com.interview.common.exception.ValidationException;
import com.interview.jpa.entity.Flight;
import com.interview.jpa.repository.FlightRepository;
import com.interview.service.flight.model.FlightSearchCriteria;
import com.interview.service.mapper.ModelMapperProvider;
import com.interview.service.mapper.flight.FlightMapperDefinition;
import com.interview.service.validator.ValidatorProvider;
import com.interview.service.validator.flight.FlightValidatorDefinition;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.interview.jpa.specification.FlightSpecifications.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FlightFetchService {

    private static final String DEFAULT_SORT_PROPERTY = "departureTime";

    private final ValidatorProvider validatorProvider;
    private final ModelMapperProvider modelMapperProvider;
    private final FlightRepository flightRepository;

    /**
     * Returns a flight by id as DTO after validating it exists.
     *
     * @param flightId flight identifier
     * @return mapped {@link FlightDto}
     * @throws ValidationException if the flight does not exist
     */
    public FlightDto get(int flightId) {
        validatorProvider.validate(new FlightValidatorDefinition.Exist(flightId));

        Flight flight = flightRepository.findByIdWithFetch(flightId);

        return modelMapperProvider.mapTo(new FlightMapperDefinition.FlightToFlightDto(flight), FlightDto.class);
    }

    /**
     * Searches flights by criteria and returns all matches (sorted by departure time ASC).
     *
     * @param criteria optional filters
     * @return list of matching {@link FlightDto}
     */
    public List<FlightDto> search(FlightSearchCriteria criteria) {
        Specification<Flight> spec = createFlightSearchSpecification(criteria);

        List<Flight> flights = flightRepository.findAll(spec, Sort.by(Sort.Direction.ASC, DEFAULT_SORT_PROPERTY));

        return flights.stream()
                .map(f -> modelMapperProvider.mapTo(new FlightMapperDefinition.FlightToFlightDto(f), FlightDto.class))
                .toList();
    }

    /**
     * Searches flights by criteria with pagination and sorting.
     *
     * @param criteria filters
     * @param page     zero-based page index
     * @param size     page size
     * @param sort     sort string, e.g. "departureTime,asc;price,desc"
     * @return page of {@link FlightDto}
     */
    public Page<FlightDto> search(FlightSearchCriteria criteria, int page, int size, String sort) {
        Specification<Flight> spec = createFlightSearchSpecification(criteria);

        Pageable pageable = buildPageable(page, size, sort);
        Page<Flight> pageResult = flightRepository.findAll(spec, pageable);

        return pageResult.map(f -> modelMapperProvider.mapTo(new FlightMapperDefinition.FlightToFlightDto(f), FlightDto.class));
    }

    private static Specification<Flight> createFlightSearchSpecification(FlightSearchCriteria criteria) {
        return Specification.allOf(withFetches(), hasDepartureAirport(criteria.getDepartureAirport())
                , hasArrivalAirport(criteria.getArrivalAirport())
                , hasStatus(criteria.getStatus())
                , hasPlaneId(criteria.getPlaneId())
                , departureBetween(criteria.getDepFrom(), criteria.getDepTo()));
    }

    private Pageable buildPageable(int page, int size, String sort) {
        if (sort == null || sort.isBlank()) {
            return PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, DEFAULT_SORT_PROPERTY));
        }

        String[] chunks = sort.split(";");
        Sort s = Sort.unsorted();
        for (String c : chunks) {
            String[] parts = c.split(",", 2);
            String prop = parts[0].trim();
            Sort.Direction dir = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()))
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            s = s.and(Sort.by(dir, prop));
        }
        return PageRequest.of(page, size, s);
    }

    public static Specification<Flight> withFetches() {
        return (root, query, cb) -> {
            // avoid fetches in count query
            if (!Long.class.equals(query.getResultType())) {
                root.fetch("plane", JoinType.LEFT);
                root.fetch("createdBy", JoinType.LEFT);
                root.fetch("updatedBy", JoinType.LEFT);
                query.distinct(true);
            }
            return cb.conjunction();
        };
    }
}
