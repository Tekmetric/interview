package com.interview.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.interview.api.request.LaborLineItemRequest;
import com.interview.api.response.LaborLineItemResponse;
import com.interview.domain.LaborLineItem;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class LaborLineItemApiMapperTest {

    private final LaborLineItemApiMapper mapper = Mappers.getMapper(LaborLineItemApiMapper.class);

    @Test
    void toDomain() {
        final UUID serviceCode = UUID.randomUUID();
        final LaborLineItemRequest request = new LaborLineItemRequest("Oil Change", 1, serviceCode);

        final LaborLineItem result = mapper.toDomain(request);

        final LaborLineItem expected = new LaborLineItem(null, "Oil Change", 1, serviceCode);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void toResponse() {
        final UUID id = UUID.randomUUID();
        final UUID serviceCode = UUID.randomUUID();
        final LaborLineItem laborLineItem = new LaborLineItem(id, "Oil Change", 1, serviceCode);

        final LaborLineItemResponse result = mapper.toResponse(laborLineItem);

        final LaborLineItemResponse expected = new LaborLineItemResponse(id, "Oil Change", 1, serviceCode);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }
}
