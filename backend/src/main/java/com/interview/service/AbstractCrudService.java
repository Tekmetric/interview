package com.interview.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.interview.persistence.entity.BaseEntity;
import com.interview.exception.DealershipException;
import com.interview.mapper.EntityMapper;

/**
 * @param <E> JPA entity type (must extend {@link BaseEntity})
 * @param <R> response DTO type
 * @param <C> create request DTO type
 * @param <U> update request DTO type
 */
public abstract class AbstractCrudService<E extends BaseEntity, R, C, U> implements CrudService<R, C, U> {

    protected abstract JpaRepository<E, UUID> getRepository();

    protected abstract EntityMapper<E, R, C, U> getMapper();

    protected abstract DealershipException notFoundException(UUID id);

    @Override
    @Transactional(readOnly = true)
    public R findById(final UUID id) {
        return getRepository().findById(id)
                .map(getMapper()::toResponse)
                .orElseThrow(() -> notFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<R> findAll(final Pageable pageable) {
        return getRepository().findAll(pageable)
                .map(getMapper()::toResponse);
    }

    @Override
    @Transactional
    public void delete(final UUID id) {
        E entity = getRepository().findById(id)
                .orElseThrow(() -> notFoundException(id));
        getRepository().delete(entity);
    }
}
