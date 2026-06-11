package com.interview.repository.mapper;

import com.interview.repository.entity.Identifiable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import org.mapstruct.TargetType;
import org.springframework.stereotype.Component;

@Component
public class EntityReferenceMapper {

    @PersistenceContext
    private EntityManager entityManager;

    public <T extends Identifiable> T toReference(UUID id, @TargetType Class<T> entityClass) {
        return id != null ? entityManager.getReference(entityClass, id) : null;
    }

    public UUID toId(Identifiable entity) {
        return entity != null ? entity.getId() : null;
    }
}
