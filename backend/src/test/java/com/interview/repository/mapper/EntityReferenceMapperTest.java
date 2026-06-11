package com.interview.repository.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.interview.repository.entity.CustomerEntity;
import com.interview.repository.entity.VehicleEntity;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EntityReferenceMapperTest {

    @Mock
    EntityManager entityManager;

    @InjectMocks
    EntityReferenceMapper entityReferenceMapper;

    @Test
    void resolveReturnsReferenceForCustomerEntity() {
        final UUID id = UUID.randomUUID();
        final CustomerEntity ref = new CustomerEntity();
        when(entityManager.getReference(CustomerEntity.class, id)).thenReturn(ref);

        final CustomerEntity result = entityReferenceMapper.toReference(id, CustomerEntity.class);

        assertThat(result).isSameAs(ref);
        verify(entityManager).getReference(CustomerEntity.class, id);
    }

    @Test
    void resolveReturnsReferenceForVehicleEntity() {
        final UUID id = UUID.randomUUID();
        final VehicleEntity ref = new VehicleEntity();
        when(entityManager.getReference(VehicleEntity.class, id)).thenReturn(ref);

        final VehicleEntity result = entityReferenceMapper.toReference(id, VehicleEntity.class);

        assertThat(result).isSameAs(ref);
        verify(entityManager).getReference(VehicleEntity.class, id);
    }

    @Test
    void resolveReturnsNullForNullId() {
        final CustomerEntity result = entityReferenceMapper.toReference(null, CustomerEntity.class);

        assertThat(result).isNull();
    }

    @Test
    void toIdReturnsEntityId() {
        final UUID id = UUID.randomUUID();
        final CustomerEntity entity = new CustomerEntity();
        entity.setId(id);

        final UUID result = entityReferenceMapper.toId(entity);

        assertThat(result).isEqualTo(id);
    }

    @Test
    void toIdReturnsNullForNullEntity() {
        final UUID result = entityReferenceMapper.toId(null);

        assertThat(result).isNull();
    }
}
