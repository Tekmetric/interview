package com.interview.runningevents.infrastructure.web.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.interview.runningevents.application.model.PaginatedResult;
import com.interview.runningevents.application.model.RunningEventQuery;
import com.interview.runningevents.application.model.SortDirection;
import com.interview.runningevents.domain.model.RunningEvent;
import com.interview.runningevents.infrastructure.web.util.DateTimeConverter;

/**
 * Mapper for converting between RunningEvent domain objects and DTOs.
 */
@Component
public class RunningEventDTOMapper {

    /**
     * Converts a domain RunningEvent to a RunningEventResponseDTO.
     *
     * @param event The domain model object to convert
     * @return The corresponding DTO for API responses
     */
    public RunningEventResponseDTO toResponseDTO(RunningEvent event) {
        if (event == null) {
            return null;
        }

        // Convert timestamp to formatted date string
        String formattedDateTime = DateTimeConverter.fromTimestamp(event.getDateTime());

        return RunningEventResponseDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .dateTime(formattedDateTime)
                .location(event.getLocation())
                .description(event.getDescription())
                .furtherInformation(event.getFurtherInformation())
                .build();
    }

    /**
     * Converts a list of domain RunningEvents to a list of RunningEventResponseDTOs.
     *
     * @param events The list of domain model objects to convert
     * @return The corresponding list of DTOs for API responses
     */
    public List<RunningEventResponseDTO> toResponseDTOList(List<RunningEvent> events) {
        if (events == null) {
            return null;
        }

        return events.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    /**
     * Converts a RunningEventRequestDTO to a domain RunningEvent.
     * Note: This method will not set the ID field, as it's typically
     * used for creating new events.
     *
     * @param requestDTO The DTO from the API request
     * @return The corresponding domain model object
     */
    public RunningEvent toDomain(RunningEventRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        // Convert date string to timestamp
        Long timestamp = DateTimeConverter.toTimestamp(requestDTO.getDateTime());

        return RunningEvent.builder()
                .name(requestDTO.getName())
                .dateTime(timestamp)
                .location(requestDTO.getLocation())
                .description(requestDTO.getDescription())
                .furtherInformation(requestDTO.getFurtherInformation())
                .build();
    }

    /**
     * Updates an existing RunningEvent domain object with values from a RunningEventRequestDTO.
     * This is typically used in update operations where we want to preserve the ID.
     *
     * @param existingEvent The existing domain model object to update
     * @param requestDTO The DTO containing the updated values
     * @return The updated domain model object
     */
    public RunningEvent updateDomainFromDTO(RunningEvent existingEvent, RunningEventRequestDTO requestDTO) {
        if (existingEvent == null || requestDTO == null) {
            return existingEvent;
        }

        // Convert date string to timestamp
        Long timestamp = DateTimeConverter.toTimestamp(requestDTO.getDateTime());

        existingEvent.setName(requestDTO.getName());
        existingEvent.setDateTime(timestamp);
        existingEvent.setLocation(requestDTO.getLocation());
        existingEvent.setDescription(requestDTO.getDescription());
        existingEvent.setFurtherInformation(requestDTO.getFurtherInformation());

        return existingEvent;
    }

    /**
     * Converts a RunningEventQueryDTO to a RunningEventQuery application model.
     *
     * @param queryDTO The query parameters from the API request
     * @return The corresponding application model query object
     */
    public RunningEventQuery toQueryModel(RunningEventQueryDTO queryDTO) {
        if (queryDTO == null) {
            return RunningEventQuery.builder().build();
        }

        // Convert date strings to timestamps if present
        Long fromTimestamp = DateTimeConverter.toTimestamp(queryDTO.getFromDate());
        Long toTimestamp = DateTimeConverter.toTimestamp(queryDTO.getToDate());

        // Parse the sort direction string to enum
        SortDirection sortDirection = SortDirection.fromString(queryDTO.getSortDirection());

        return RunningEventQuery.builder()
                .fromDate(fromTimestamp)
                .toDate(toTimestamp)
                .page(queryDTO.getPage() != null ? queryDTO.getPage() : 0)
                .pageSize(queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 20)
                .sortBy(queryDTO.getSortBy() != null ? queryDTO.getSortBy() : "dateTime")
                .sortDirection(sortDirection)
                .build();
    }

    /**
     * Converts a PaginatedResult of RunningEvents to a PaginatedResponseDTO of RunningEventResponseDTOs.
     *
     * @param paginatedResult The paginated result from the application layer
     * @return The corresponding paginated response DTO for the API
     */
    public PaginatedResponseDTO<RunningEventResponseDTO> toPaginatedResponseDTO(
            PaginatedResult<RunningEvent> paginatedResult) {
        if (paginatedResult == null) {
            return null;
        }

        List<RunningEventResponseDTO> dtoItems = toResponseDTOList(paginatedResult.getItems());

        return PaginatedResponseDTO.<RunningEventResponseDTO>builder()
                .items(dtoItems)
                .totalItems(paginatedResult.getTotalItems())
                .page(paginatedResult.getPage())
                .pageSize(paginatedResult.getPageSize())
                .totalPages(paginatedResult.getTotalPages())
                .hasPrevious(paginatedResult.isHasPrevious())
                .hasNext(paginatedResult.isHasNext())
                .build();
    }
}
