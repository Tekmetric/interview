package com.interview.command.handler;

import com.interview.command.repository.WidgetCommandRepository;
import com.interview.common.events.WidgetDeletedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteWidgetHandler {

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
    public boolean handle(Long id) {
        return widgetCommandRepository.findById(id)
                .map(widget -> {
                    widgetCommandRepository.delete(widget);

                    // Publish event for query database synchronization
                    eventPublisher.publishEvent(new WidgetDeletedEvent(id));

                    return true;
                })
                .orElse(false);
    }
}
