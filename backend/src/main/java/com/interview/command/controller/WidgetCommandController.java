package com.interview.command.controller;

import com.interview.command.dto.CreateWidgetCommand;
import com.interview.command.dto.UpdateWidgetCommand;
import com.interview.command.handler.CreateWidgetHandler;
import com.interview.command.handler.DeleteWidgetHandler;
import com.interview.command.handler.UpdateWidgetHandler;
import com.interview.common.entity.Widget;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/widgets")
public class WidgetCommandController {

    private final CreateWidgetHandler createWidgetHandler;
    private final UpdateWidgetHandler updateWidgetHandler;
    private final DeleteWidgetHandler deleteWidgetHandler;

    @Autowired
    public WidgetCommandController(CreateWidgetHandler createWidgetHandler,
                                   UpdateWidgetHandler updateWidgetHandler,
                                   DeleteWidgetHandler deleteWidgetHandler) {
        this.createWidgetHandler = createWidgetHandler;
        this.updateWidgetHandler = updateWidgetHandler;
        this.deleteWidgetHandler = deleteWidgetHandler;
    }

    @PostMapping
    public ResponseEntity<Widget> createWidget(@Valid @RequestBody CreateWidgetCommand command) {
        Widget createdWidget = createWidgetHandler.handle(command);

        /*
        WidgetDto is from the query module, and should not be usable here. Un-comment the below
        line to see Modulith module boundary errors when build/testing. This proves Modulith is working.
        */

        //com.interview.query.dto.WidgetDto dto = new com.interview.query.dto.WidgetDto();

        return ResponseEntity.status(HttpStatus.CREATED).body(createdWidget);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Widget> updateWidget(@PathVariable Long id, @Valid @RequestBody UpdateWidgetCommand command) {
        return updateWidgetHandler.handle(id, command)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWidget(@PathVariable Long id) {
        if (deleteWidgetHandler.handle(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
