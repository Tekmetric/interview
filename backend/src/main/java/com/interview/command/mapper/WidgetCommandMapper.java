package com.interview.command.mapper;

import com.interview.command.dto.CreateWidgetCommand;
import com.interview.command.dto.UpdateWidgetCommand;
import com.interview.common.entity.Widget;
import com.interview.common.events.WidgetCreatedEvent;
import com.interview.common.events.WidgetUpdatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WidgetCommandMapper {

    @Mapping(target = "id", ignore = true)
    Widget toEntity(CreateWidgetCommand command);

    @Mapping(target = "id", ignore = true)
    void updateEntity(UpdateWidgetCommand command, @MappingTarget Widget widget);

    WidgetCreatedEvent toCreatedEvent(Widget widget);

    WidgetUpdatedEvent toUpdatedEvent(Widget widget);
}
