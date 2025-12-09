package com.interview.query.handler;

import com.interview.query.dto.WidgetDto;
import com.interview.query.mapper.WidgetQueryMapper;
import com.interview.query.repository.WidgetQueryRepository;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetWidgetByIdHandler {

    private static final Logger log = LoggerFactory.getLogger(GetWidgetByIdHandler.class);

    private final WidgetQueryRepository widgetQueryRepository;
    private final WidgetQueryMapper mapper;

    @Autowired
    public GetWidgetByIdHandler(WidgetQueryRepository widgetQueryRepository,
                                WidgetQueryMapper mapper) {
        this.widgetQueryRepository = widgetQueryRepository;
        this.mapper = mapper;
    }

    @Cacheable(value = "widgets", key = "#id")
    @Timed(value = "widget.getById", description = "Time taken to retrieve a widget by ID")
    public Optional<WidgetDto> handle(Long id) {
        log.debug("Handling get widget by id query for id: {}", id);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Optional<WidgetDto> result = widgetQueryRepository.findById(id)
                .map(mapper::toDto);
        if (result.isPresent()) {
            log.info("Retrieved widget from query database with id: {}", id);
        } else {
            log.info("Widget not found in query database with id: {}", id);
        }
        return result;
    }
}
