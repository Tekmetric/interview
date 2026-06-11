package com.interview.repository.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.interview.domain.LaborLineItem;
import com.interview.repository.entity.LaborLineItemEntity;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class LaborLineItemEntityMapperTest {

    private final LaborLineItemEntityMapper mapper = Mappers.getMapper(LaborLineItemEntityMapper.class);

    @Test
    void toEntity() {
        final UUID serviceCode = UUID.randomUUID();
        final LaborLineItem laborLineItem = new LaborLineItem(UUID.randomUUID(), "Oil Change", 1, serviceCode);

        final LaborLineItemEntity result = mapper.toEntity(laborLineItem);

        final LaborLineItemEntity expected = new LaborLineItemEntity();
        expected.setName("Oil Change");
        expected.setQuantity(1);
        expected.setServiceCode(serviceCode);
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id", "workOrder")
                .isEqualTo(expected);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getWorkOrder()).isNull();
    }

    @Test
    void toDomain() {
        final UUID id = UUID.randomUUID();
        final UUID serviceCode = UUID.randomUUID();

        final LaborLineItemEntity entity = new LaborLineItemEntity();
        entity.setId(id);
        entity.setName("Oil Change");
        entity.setQuantity(1);
        entity.setServiceCode(serviceCode);

        final LaborLineItem result = mapper.toDomain(entity);

        final LaborLineItem expected = new LaborLineItem(id, "Oil Change", 1, serviceCode);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toDomainList() {
        final UUID id1 = UUID.randomUUID();
        final UUID serviceCode1 = UUID.randomUUID();
        final LaborLineItemEntity entity1 = new LaborLineItemEntity();
        entity1.setId(id1);
        entity1.setName("Oil Change");
        entity1.setQuantity(1);
        entity1.setServiceCode(serviceCode1);

        final UUID id2 = UUID.randomUUID();
        final UUID serviceCode2 = UUID.randomUUID();
        final LaborLineItemEntity entity2 = new LaborLineItemEntity();
        entity2.setId(id2);
        entity2.setName("Tire Rotation");
        entity2.setQuantity(4);
        entity2.setServiceCode(serviceCode2);

        final List<LaborLineItem> result = mapper.toDomain(Set.of(entity1, entity2));

        assertThat(result).hasSize(2);
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(
                        new LaborLineItem(id1, "Oil Change", 1, serviceCode1),
                        new LaborLineItem(id2, "Tire Rotation", 4, serviceCode2)));
    }
}
