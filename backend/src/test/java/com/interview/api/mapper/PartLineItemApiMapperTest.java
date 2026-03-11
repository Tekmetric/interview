package com.interview.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.interview.api.request.PartLineItemRequest;
import com.interview.api.response.PartLineItemResponse;
import com.interview.domain.PartLineItem;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class PartLineItemApiMapperTest {

    private final PartLineItemApiMapper mapper = Mappers.getMapper(PartLineItemApiMapper.class);

    @Test
    void toDomain() {
        final UUID partNumber = UUID.randomUUID();
        final PartLineItemRequest request = new PartLineItemRequest("Oil Filter", 2, partNumber);

        final PartLineItem result = mapper.toDomain(request);

        final PartLineItem expected = new PartLineItem(null, "Oil Filter", 2, partNumber);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toResponse() {
        final UUID id = UUID.randomUUID();
        final UUID partNumber = UUID.randomUUID();
        final PartLineItem partLineItem = new PartLineItem(id, "Oil Filter", 2, partNumber);

        final PartLineItemResponse result = mapper.toResponse(partLineItem);

        final PartLineItemResponse expected = new PartLineItemResponse(id, "Oil Filter", 2, partNumber);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }
}
