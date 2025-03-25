package com.interview.runningevents.infrastructure.web.dto;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.interview.runningevents.application.model.PaginatedResult;
import com.interview.runningevents.application.model.RunningEventQuery;
import com.interview.runningevents.application.model.SortDirection;
import com.interview.runningevents.domain.model.RunningEvent;

/**
 * Mapper for converting between RunningEvent domain objects and DTOs.
 */
@Component
public class RunningEventDTOMapper {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

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

        String formattedDateTime = null;
        if (event.getDateTime() != null) {
            Instant instant = Instant.ofEpochMilli(event.getDateTime());
            formattedDateTime = DATE_FORMATTER.format(instant);
        }

        return RunningEventResponseDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .dateTime(event.getDateTime())
                .location(event.getLocation())
                .description(event.getDescription())
                .furtherInformation(event.getFurtherInformation())
                .formattedDateTime(formattedDateTime)
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

        return RunningEvent.builder()
                .name(requestDTO.getName())
                .dateTime(requestDTO.getDateTime())
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

        existingEvent.setName(requestDTO.getName());
        existingEvent.setDateTime(requestDTO.getDateTime());
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

        // Parse the sort direction string to enum
        SortDirection sortDirection = SortDirection.fromString(queryDTO.getSortDirection());

        return RunningEventQuery.builder()
                .fromDate(queryDTO.getFromDate())
                .toDate(queryDTO.getToDate())
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
