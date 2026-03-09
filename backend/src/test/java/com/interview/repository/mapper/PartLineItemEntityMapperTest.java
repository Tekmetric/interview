package com.interview.repository.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.interview.domain.PartLineItem;
import com.interview.repository.entity.PartLineItemEntity;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class PartLineItemEntityMapperTest {

    private final PartLineItemEntityMapper mapper = Mappers.getMapper(PartLineItemEntityMapper.class);

    @Test
    void toEntity() {
        final UUID partNumber = UUID.randomUUID();
        final PartLineItem partLineItem = new PartLineItem(UUID.randomUUID(), "Oil Filter", 2, partNumber);

        final PartLineItemEntity result = mapper.toEntity(partLineItem);

        final PartLineItemEntity expected = new PartLineItemEntity();
        expected.setName("Oil Filter");
        expected.setQuantity(2);
        expected.setPartNumber(partNumber);
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
        final UUID partNumber = UUID.randomUUID();

        final PartLineItemEntity entity = new PartLineItemEntity();
        entity.setId(id);
        entity.setName("Oil Filter");
        entity.setQuantity(2);
        entity.setPartNumber(partNumber);

        final PartLineItem result = mapper.toDomain(entity);

        final PartLineItem expected = new PartLineItem(id, "Oil Filter", 2, partNumber);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toDomainList() {
        final UUID id1 = UUID.randomUUID();
        final UUID partNumber1 = UUID.randomUUID();
        final PartLineItemEntity entity1 = new PartLineItemEntity();
        entity1.setId(id1);
        entity1.setName("Oil Filter");
        entity1.setQuantity(2);
        entity1.setPartNumber(partNumber1);

        final UUID id2 = UUID.randomUUID();
        final UUID partNumber2 = UUID.randomUUID();
        final PartLineItemEntity entity2 = new PartLineItemEntity();
        entity2.setId(id2);
        entity2.setName("Air Filter");
        entity2.setQuantity(1);
        entity2.setPartNumber(partNumber2);

        final List<PartLineItem> result = mapper.toDomain(Set.of(entity1, entity2));

        assertThat(result).hasSize(2);
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(
                        new PartLineItem(id1, "Oil Filter", 2, partNumber1),
                        new PartLineItem(id2, "Air Filter", 1, partNumber2)));
    }
}
