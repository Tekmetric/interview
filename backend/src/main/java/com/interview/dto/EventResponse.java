package com.interview.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Event response with venue and performer details")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventResponse {

    @Schema(description = "Event ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Event name", example = "Taylor Swift Concert")
    private String name;

    @Schema(description = "Event description", example = "An amazing concert featuring Taylor Swift")
    private String description;

    @Schema(description = "Event date and time", example = "2025-12-31T20:00:00")
    private LocalDateTime eventDate;

    @Schema(description = "Event location", example = "Madison Square Garden, New York")
    private String location;

    @Schema(description = "Venue information")
    private VenueInfo venue;

    @Schema(description = "List of performers")
    private Set<PerformerInfo> performers;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    // Nested DTOs for venue and performer
    @Schema(description = "Venue information")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class VenueInfo {
        @Schema(description = "Venue ID")
        private UUID id;

        @Schema(description = "Venue name")
        private String name;

        @Schema(description = "Venue address")
        private String address;

        @Schema(description = "City")
        private String city;

        @Schema(description = "State")
        private String state;

        @Schema(description = "ZIP code")
        private String zipCode;

        @Schema(description = "Creation timestamp")
        private LocalDateTime createdAt;

        @Schema(description = "Last update timestamp")
        private LocalDateTime updatedAt;

        // Constructors
        public VenueInfo() {
        }

        public VenueInfo(UUID id, String name, String address, String city, String state, String zipCode) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.city = city;
            this.state = state;
            this.zipCode = zipCode;
        }

        // Getters and Setters
        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }

    }

    @Schema(description = "Performer information")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PerformerInfo {
        @Schema(description = "Performer ID")
        private UUID id;

        @Schema(description = "Performer name")
        private String name;

        @Schema(description = "Genre")
        private String genre;

        @Schema(description = "Biography")
        private String bio;

        @Schema(description = "Creation timestamp")
        private LocalDateTime createdAt;

        @Schema(description = "Last update timestamp")
        private LocalDateTime updatedAt;

        // Constructors
        public PerformerInfo() {
        }

        public PerformerInfo(UUID id, String name, String genre, String bio) {
            this.id = id;
            this.name = name;
            this.genre = genre;
            this.bio = bio;
        }

        // Getters and Setters
        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGenre() {
            return genre;
        }

        public void setGenre(String genre) {
            this.genre = genre;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }

    // Constructors
    public EventResponse() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public VenueInfo getVenue() {
        return venue;
    }

    public void setVenue(VenueInfo venue) {
        this.venue = venue;
    }

    public Set<PerformerInfo> getPerformers() {
        return performers;
    }

    public void setPerformers(Set<PerformerInfo> performers) {
        this.performers = performers;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}