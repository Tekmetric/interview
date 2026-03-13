package com.interview.command.handler;

import com.interview.command.dto.CreateWidgetCommand;
import com.interview.command.mapper.WidgetCommandMapper;
import com.interview.command.repository.WidgetCommandRepository;
import com.interview.common.entity.Widget;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateWidgetHandler {

    private static final Logger log = LoggerFactory.getLogger(CreateWidgetHandler.class);

    private final WidgetCommandRepository widgetCommandRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final WidgetCommandMapper mapper;

    @Autowired
    public CreateWidgetHandler(WidgetCommandRepository widgetCommandRepository,
                              ApplicationEventPublisher eventPublisher,
                              WidgetCommandMapper mapper) {
        this.widgetCommandRepository = widgetCommandRepository;
        this.eventPublisher = eventPublisher;
        this.mapper = mapper;
    }

    @Transactional("commandTransactionManager")
    @CacheEvict(value = "allWidgets", allEntries = true)
    @Timed(value = "widget.create", description = "Time taken to create a widget")
    public Widget handle(CreateWidgetCommand command) {
        log.debug("Handling create widget command for: {}", command.getName());
        Widget widget = mapper.toEntity(command);
        Widget savedWidget = widgetCommandRepository.save(widget);
        log.info("Widget created in command database with id: {}", savedWidget.getId());

        // Publish event for query database synchronization
        eventPublisher.publishEvent(mapper.toCreatedEvent(savedWidget));
        log.debug("Published WidgetCreatedEvent for widget id: {}", savedWidget.getId());

        return savedWidget;
    }
}
