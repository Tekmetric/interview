package com.interview.query.handler;

import com.interview.query.dto.WidgetDto;
import com.interview.query.mapper.WidgetQueryMapper;
import com.interview.query.repository.WidgetQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "widgets", key = "#id")
    public Optional<WidgetDto> handle(Long id) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return widgetQueryRepository.findById(id)
                .map(mapper::toDto);
    }
}
