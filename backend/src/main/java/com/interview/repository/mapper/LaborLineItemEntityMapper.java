package com.interview.repository.mapper;

import com.interview.domain.LaborLineItem;
import com.interview.repository.entity.LaborLineItemEntity;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LaborLineItemEntityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "workOrder", ignore = true)
    LaborLineItemEntity toEntity(LaborLineItem laborLineItem);

    LaborLineItem toDomain(LaborLineItemEntity entity);

    List<LaborLineItem> toDomain(Set<LaborLineItemEntity> entities);
}
