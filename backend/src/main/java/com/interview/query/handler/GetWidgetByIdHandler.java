package com.interview.query.handler;

import com.interview.query.dto.WidgetDto;
import com.interview.query.mapper.WidgetQueryMapper;
import com.interview.query.repository.WidgetQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetWidgetByIdHandler {

    private final WidgetQueryRepository widgetQueryRepository;
    private final WidgetQueryMapper mapper;

    @Autowired
    public GetWidgetByIdHandler(WidgetQueryRepository widgetQueryRepository,
                                WidgetQueryMapper mapper) {
        this.widgetQueryRepository = widgetQueryRepository;
        this.mapper = mapper;
    }

    public Optional<WidgetDto> handle(Long id) {
        return widgetQueryRepository.findById(id)
                .map(mapper::toDto);
    }
}
