package com.interview.command.handler;

import com.interview.command.dto.UpdateWidgetCommand;
import com.interview.command.mapper.WidgetCommandMapper;
import com.interview.command.repository.WidgetCommandRepository;
import com.interview.common.entity.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UpdateWidgetHandler {

    private static final Logger log = LoggerFactory.getLogger(UpdateWidgetHandler.class);

    private final WidgetCommandRepository widgetCommandRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final WidgetCommandMapper mapper;

    @Autowired
    public UpdateWidgetHandler(WidgetCommandRepository widgetCommandRepository,
                              ApplicationEventPublisher eventPublisher,
                              WidgetCommandMapper mapper) {
        this.widgetCommandRepository = widgetCommandRepository;
        this.eventPublisher = eventPublisher;
        this.mapper = mapper;
    }

    @Transactional("commandTransactionManager")
    @Caching(evict = {
        @CacheEvict(value = "widgets", key = "#id"),
        @CacheEvict(value = "allWidgets", allEntries = true)
    })
    public Optional<Widget> handle(Long id, UpdateWidgetCommand command) {
        log.debug("Handling update widget command for id: {}", id);
        return widgetCommandRepository.findById(id)
                .map(widget -> {
                    mapper.updateEntity(command, widget);
                    Widget updatedWidget = widgetCommandRepository.save(widget);
                    log.info("Widget updated in command database with id: {}", updatedWidget.getId());

                    // Publish event for query database synchronization
                    eventPublisher.publishEvent(mapper.toUpdatedEvent(updatedWidget));
                    log.debug("Published WidgetUpdatedEvent for widget id: {}", updatedWidget.getId());

                    return updatedWidget;
                });
    }
}
