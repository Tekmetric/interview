package com.interview.model;

import java.time.LocalDateTime;

public class EventDocument {

    private String id;
    private String name;
    private String description;
    private LocalDateTime eventDate;
    private String location;
    private LocalDateTime createdAt;

    // No-arg constructor for Jackson
    public EventDocument() {
    }

    // ✅ Constructor
    public EventDocument(String id, String name, String description,
                         LocalDateTime eventDate, String location,
                         LocalDateTime createdAt) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.createdAt = createdAt;
    }

    // Static mapper method
    public static EventDocument fromEvent(Event event) {
        return new EventDocument(
                event.getId().toString(),
                event.getName(),
                event.getDescription(),
                event.getEventDate(),
                event.getLocation(),
                event.getCreatedAt()
        );
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDateTime getEventDate() { return eventDate; }
    public String getLocation() { return location; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters for Jackson
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
    public void setLocation(String location) { this.location = location; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
