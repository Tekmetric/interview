package com.interview.query.handler;

import com.interview.query.dto.WidgetDto;
import com.interview.query.mapper.WidgetQueryMapper;
import com.interview.query.repository.WidgetQueryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetAllWidgetsHandler {

    private static final Logger log = LoggerFactory.getLogger(GetAllWidgetsHandler.class);

    private final WidgetQueryRepository widgetQueryRepository;
    private final WidgetQueryMapper mapper;

    @Autowired
    public GetAllWidgetsHandler(WidgetQueryRepository widgetQueryRepository,
                                WidgetQueryMapper mapper) {
        this.widgetQueryRepository = widgetQueryRepository;
        this.mapper = mapper;
    }

    @Cacheable("allWidgets")
    public List<WidgetDto> handle() {
        log.debug("Handling get all widgets query");
        List<WidgetDto> widgets = widgetQueryRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        log.info("Retrieved {} widgets from query database", widgets.size());
        return widgets;
    }
}
