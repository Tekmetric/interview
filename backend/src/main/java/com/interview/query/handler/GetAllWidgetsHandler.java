package com.interview.query.handler;

import com.interview.query.dto.WidgetDto;
import com.interview.query.mapper.WidgetQueryMapper;
import com.interview.query.repository.WidgetQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetAllWidgetsHandler {

    private final WidgetQueryRepository widgetQueryRepository;
    private final WidgetQueryMapper mapper;

    @Autowired
    public GetAllWidgetsHandler(WidgetQueryRepository widgetQueryRepository,
                                WidgetQueryMapper mapper) {
        this.widgetQueryRepository = widgetQueryRepository;
        this.mapper = mapper;
    }

    public List<WidgetDto> handle() {
        return widgetQueryRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
