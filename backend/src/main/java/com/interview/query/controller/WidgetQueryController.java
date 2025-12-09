package com.interview.query.controller;

import com.interview.query.dto.WidgetDto;
import com.interview.query.handler.GetAllWidgetsHandler;
import com.interview.query.handler.GetWidgetByIdHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/widgets")
public class WidgetQueryController {

    private static final Logger log = LoggerFactory.getLogger(WidgetQueryController.class);

    private final GetAllWidgetsHandler getAllWidgetsHandler;
    private final GetWidgetByIdHandler getWidgetByIdHandler;

    @Autowired
    public WidgetQueryController(GetAllWidgetsHandler getAllWidgetsHandler,
                                 GetWidgetByIdHandler getWidgetByIdHandler) {
        this.getAllWidgetsHandler = getAllWidgetsHandler;
        this.getWidgetByIdHandler = getWidgetByIdHandler;
    }

    @GetMapping
    public ResponseEntity<List<WidgetDto>> getAllWidgets() {
        log.info("Received request to get all widgets");
        List<WidgetDto> widgets = getAllWidgetsHandler.handle();
        log.info("Returning {} widgets", widgets.size());
        return ResponseEntity.ok(widgets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WidgetDto> getWidgetById(@PathVariable Long id) {
        log.info("Received request to get widget by id: {}", id);
        return getWidgetByIdHandler.handle(id)
                .map(widget -> {
                    log.info("Widget found with id: {}", id);
                    return ResponseEntity.ok(widget);
                })
                .orElseGet(() -> {
                    log.warn("Widget not found with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }
}
