package com.interview.query.controller;

import com.interview.query.dto.WidgetDto;
import com.interview.query.handler.GetAllWidgetsHandler;
import com.interview.query.handler.GetWidgetByIdHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/widgets")
public class WidgetQueryController {

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
        List<WidgetDto> widgets = getAllWidgetsHandler.handle();
        return ResponseEntity.ok(widgets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WidgetDto> getWidgetById(@PathVariable Long id) {
        return getWidgetByIdHandler.handle(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
