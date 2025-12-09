package com.interview.query.mapper;

import com.interview.common.entity.Widget;
import com.interview.query.dto.WidgetDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WidgetQueryMapper {

    WidgetDto toDto(Widget widget);
}
