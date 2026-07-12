package com.interview.dto;

import com.interview.model.Event;
import com.interview.model.Performer;
import com.interview.model.Venue;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EventMapper {

    public EventResponse toResponse(Event event) {
        return toResponse(event, true);
    }

    public EventResponse toResponse(Event event, boolean isAdmin) {
        if (event == null) {
            return null;
        }

        EventResponse response = new EventResponse();
        response.setId(event.getId());
        response.setName(event.getName());
        response.setDescription(event.getDescription());
        response.setEventDate(event.getEventDate());
        response.setLocation(event.getLocation());

        // Only set timestamps if user is admin
        if (isAdmin) {
            response.setCreatedAt(event.getCreatedAt());
            response.setUpdatedAt(event.getUpdatedAt());
        }

        // Map venue
        if (event.getVenue() != null) {
            response.setVenue(toVenueInfo(event.getVenue(), isAdmin));
        }

        // Map performers
        if (event.getPerformers() != null) {
            Set<EventResponse.PerformerInfo> performerInfos = event.getPerformers().stream()
                    .map(p -> toPerformerInfo(p, isAdmin))
                    .collect(Collectors.toSet());
            response.setPerformers(performerInfos);
        }

        return response;
    }

    public EventSummaryResponse toSummaryResponse(Event event) {
        return toSummaryResponse(event, true);
    }

    public EventSummaryResponse toSummaryResponse(Event event, boolean isAdmin) {
        if (event == null) {
            return null;
        }

        EventSummaryResponse response = new EventSummaryResponse();
        response.setId(event.getId());
        response.setName(event.getName());
        response.setDescription(event.getDescription());
        response.setEventDate(event.getEventDate());
        response.setLocation(event.getLocation());

        // Only set timestamps if user is admin
        if (isAdmin) {
            response.setCreatedAt(event.getCreatedAt());
            response.setUpdatedAt(event.getUpdatedAt());
        }

        return response;
    }

    public Event toEntity(EventRequest request) {
        if (request == null) {
            return null;
        }

        Event event = new Event();
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setLocation(request.getLocation());

        return event;
    }

    public void updateEntityFromRequest(Event event, EventRequest request) {
        if (event == null || request == null) {
            return;
        }

        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setLocation(request.getLocation());
    }

    private EventResponse.VenueInfo toVenueInfo(Venue venue, boolean isAdmin) {
        if (venue == null) {
            return null;
        }

        EventResponse.VenueInfo venueInfo = new EventResponse.VenueInfo(
                venue.getId(),
                venue.getName(),
                venue.getAddress(),
                venue.getCity(),
                venue.getState(),
                venue.getZipCode()
        );

        // Only set timestamps if user is admin
        if (isAdmin) {
            venueInfo.setCreatedAt(venue.getCreatedAt());
            venueInfo.setUpdatedAt(venue.getUpdatedAt());
        }

        return venueInfo;
    }

    private EventResponse.PerformerInfo toPerformerInfo(Performer performer, boolean isAdmin) {
        if (performer == null) {
            return null;
        }

        EventResponse.PerformerInfo performerInfo = new EventResponse.PerformerInfo(
                performer.getId(),
                performer.getName(),
                performer.getGenre(),
                performer.getBio()
        );

        // Only set timestamps if user is admin
        if (isAdmin) {
            performerInfo.setCreatedAt(performer.getCreatedAt());
            performerInfo.setUpdatedAt(performer.getUpdatedAt());
        }

        return performerInfo;
    }
}