package com.interview.command.handler;

import com.interview.command.dto.CreateWidgetCommand;
import com.interview.command.mapper.WidgetCommandMapper;
import com.interview.command.repository.WidgetCommandRepository;
import com.interview.common.entity.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateWidgetHandler {

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
    public Widget handle(CreateWidgetCommand command) {
        Widget widget = mapper.toEntity(command);
        Widget savedWidget = widgetCommandRepository.save(widget);

        // Publish event for query database synchronization
        eventPublisher.publishEvent(mapper.toCreatedEvent(savedWidget));

        return savedWidget;
    }
}
