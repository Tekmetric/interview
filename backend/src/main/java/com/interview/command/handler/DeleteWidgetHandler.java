package com.interview.command.handler;

import com.interview.command.repository.WidgetCommandRepository;
import com.interview.common.events.WidgetDeletedEvent;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteWidgetHandler {

    private static final Logger log = LoggerFactory.getLogger(DeleteWidgetHandler.class);

    private final WidgetCommandRepository widgetCommandRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public DeleteWidgetHandler(WidgetCommandRepository widgetCommandRepository,
                              ApplicationEventPublisher eventPublisher) {
        this.widgetCommandRepository = widgetCommandRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional("commandTransactionManager")
    @Caching(evict = {
        @CacheEvict(value = "widgets", key = "#id"),
        @CacheEvict(value = "allWidgets", allEntries = true)
    })
    @Timed(value = "widget.delete", description = "Time taken to delete a widget")
    public boolean handle(Long id) {
        log.debug("Handling delete widget command for id: {}", id);
        return widgetCommandRepository.findById(id)
                .map(widget -> {
                    widgetCommandRepository.delete(widget);
                    log.info("Widget deleted from command database with id: {}", id);

                    // Publish event for query database synchronization
                    eventPublisher.publishEvent(new WidgetDeletedEvent(id));
                    log.debug("Published WidgetDeletedEvent for widget id: {}", id);

                    return true;
                })
                .orElse(false);
    }
}
