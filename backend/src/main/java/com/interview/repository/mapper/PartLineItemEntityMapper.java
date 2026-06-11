package com.interview.repository.mapper;

import com.interview.domain.PartLineItem;
import com.interview.repository.entity.PartLineItemEntity;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PartLineItemEntityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "workOrder", ignore = true)
    PartLineItemEntity toEntity(PartLineItem partLineItem);

    PartLineItem toDomain(PartLineItemEntity entity);

    List<PartLineItem> toDomain(Set<PartLineItemEntity> entities);
}
