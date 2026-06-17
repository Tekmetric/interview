package com.interview.service;

import com.interview.dto.EntityType;
import com.interview.dto.NotificationAction;
import com.interview.dto.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for sending real-time notifications via WebSocket and JMS.
 *
 * Sends notifications when entities are created, updated, or deleted.
 */
@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * Send notification for an entity change.
     *
     * @param action The action performed (CREATE, UPDATE, DELETE)
     * @param entityType The type of entity (ARTIST, SONG, ALBUM)
     * @param entityId The ID of the entity
     */
    public void sendNotification(NotificationAction action, EntityType entityType, Long entityId) {
        NotificationMessage message = new NotificationMessage(action, entityType, entityId);

        // Send via WebSocket to all subscribed clients
        String destination = "/topic/" + entityType.name().toLowerCase() + "s";
        messagingTemplate.convertAndSend(destination, message);

        // Send via JMS for internal processing
        String queue = entityType.name().toLowerCase() + ".queue";
        jmsTemplate.convertAndSend(queue, message);
    }

    /**
     * Send notification for artist changes.
     */
    public void notifyArtistChange(NotificationAction action, Long artistId) {
        sendNotification(action, EntityType.ARTIST, artistId);
    }

    /**
     * Send notification for song changes.
     */
    public void notifySongChange(NotificationAction action, Long songId) {
        sendNotification(action, EntityType.SONG, songId);
    }

    /**
     * Send notification for album changes.
     */
    public void notifyAlbumChange(NotificationAction action, Long albumId) {
        sendNotification(action, EntityType.ALBUM, albumId);
    }
}
