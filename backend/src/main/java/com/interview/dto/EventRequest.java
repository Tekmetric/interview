package com.interview.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Event creation/update request")
public class EventRequest {

    @Schema(description = "Event name", example = "Taylor Swift Concert", required = true)
    @NotBlank(message = "Event name is required")
    @Size(min = 1, max = 255, message = "Event name must be between 1 and 255 characters")
    private String name;

    @Schema(description = "Event description", example = "An amazing concert featuring Taylor Swift")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Schema(description = "Event date and time", example = "2025-12-31T20:00:00", required = true)
    @NotNull(message = "Event date is required")
    @Future(message = "Event date must be in the future")
    private LocalDateTime eventDate;

    @Schema(description = "Event location", example = "Madison Square Garden, New York", required = true)
    @NotBlank(message = "Location is required")
    @Size(min = 1, max = 255, message = "Location must be between 1 and 255 characters")
    private String location;

    @Schema(description = "Venue ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID venueId;

    @Schema(description = "Set of performer IDs", example = "[\"123e4567-e89b-12d3-a456-426614174001\"]")
    private Set<UUID> performerIds;

    // Constructors
    public EventRequest() {
    }

    public EventRequest(String name, String description, LocalDateTime eventDate, String location, UUID venueId, Set<UUID> performerIds) {
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.venueId = venueId;
        this.performerIds = performerIds;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public UUID getVenueId() {
        return venueId;
    }

    public void setVenueId(UUID venueId) {
        this.venueId = venueId;
    }

    public Set<UUID> getPerformerIds() {
        return performerIds;
    }

    public void setPerformerIds(Set<UUID> performerIds) {
        this.performerIds = performerIds;
    }
}