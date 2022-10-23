package com.interview.domain.service.common;

import com.interview.application.aspect.log.annotations.ExcludeFromLoggingAspect;
import com.interview.application.rest.v1.common.dto.AbstractAuditingDto;
import com.interview.domain.exception.*;
import com.interview.domain.model.common.AbstractAuditingEntity;
import com.interview.domain.model.common.GenericID;
import com.interview.domain.repository.EntityRepository;
import com.interview.domain.service.common.mapper.Mapper;
import com.interview.domain.service.common.mapper.MapperService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Basic service for CRUD resources
 * @param <ENTITY> the entity that is saved in the database
 * @param <ID> the id
 * @param <DTO> the dto that is returned to the client.
 */
@Transactional
@Slf4j
public abstract class AbstractCRUDService<ENTITY extends AbstractAuditingEntity<ID>,
        ID extends Serializable & Comparable<ID>,
        DTO extends AbstractAuditingDto<ID>> extends AbstractService {

    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 25;

    protected final Class<? extends AbstractAuditingEntity<ID>> entityClass;
    @Autowired
    private MapperService mapperService;

    public AbstractCRUDService(final Class<ENTITY> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityRepository<ENTITY, ID> getRepository();

    /**
     * To be implemented by service subclasses , in order to correctly instantiate a new entity
     */
    protected ENTITY newEntity(final DTO entityDTO) {
        return convertToEntity(entityDTO);
    }

    /**
     * Method is called just before we save on the repository for a new entity
     *
     * @param entity - the ENTITY of the saved domain object. Must not be null
     */
    protected void onBeforeSaveNewEntity(final ENTITY entity) {
        //DO NOTHING BY DEFAULT
    }

    /**
     * Method is called just before we save(update) on the repository for an existing  entity
     *
     * @param entity - the ENTITY of the saved domain object. Must not be null
     */
    protected void onBeforeSaveEntity(final ENTITY entity) {
        //DO NOTHING BY DEFAULT
    }

    /**
     * Method is called just after save on the repository for a new entity
     *
     * @param entity - the ENTITY of the saved domain object. Must not be null
     */
    protected void onAfterSaveNewEntity(final ENTITY entity) {
        //DO NOTHING BY DEFAULT
    }

    protected void onAfterSaveEntity(final ENTITY entity) {
        //DO NOTHING BY DEFAULT
    }

    /**
     * Method is called just before we save on the repository for a new entity
     *
     * @param entity - the ENTITY of the saved domain object. Must not be null
     */
    protected void onBeforeDeleteEntity(final ENTITY entity) {
        //DO NOTHING BY DEFAULT
    }

    /**
     * Method is called just after save on the repository for a new entity
     *
     * @param entity - the ENTITY of the saved domain object. Must not be null
     */
    protected void onAfterDeleteEntity(final ENTITY entity) {
        //DO NOTHING BY DEFAULT
    }

    /**
     * Method to set default sorting for find all
     *
     * @return null by default, no sorting
     */
    protected Sort getDefaultSort() {
        return null;
    }

    /**
     * Method for customizing DTO
     */
    protected void customizeDto(final DTO dto, final ENTITY entity) {
    }

    // UPDATE //
    public DTO updateDTO(@NonNull final ID id, @NonNull final DTO dto) {
        dto.setId(id);
        return saveDTO(dto);
    }

    // SAVE //
    public DTO saveDTO(@NonNull final DTO dto) {
        try {
            final boolean isNewEntityFlag = isNewEntity(dto);
            final ENTITY entity = isNewEntityFlag ? newEntity(dto) : getMapperService().updateEntityFromDTO(dto, get(dto.getId()));
            if (entity == null) {
                log.error("This is not an valid entity to be saved. The request dto was [{}]", dto);
                throw new ApplicationException(new ErrorDetail(ErrorCode.BAD_REQUEST));
            }
            final ENTITY newEntity = save(entity);
            return convertToDto(newEntity);
        } catch (final Exception ex) {
            throw processException(ex, () -> {
                log.error("Error occurred while saving entity [{}] with using object [{}]!", entityClass, dto, ex);
                throw new ApplicationException(new ErrorDetail(ErrorCode.INTERNAL_ERROR));
            });
        }
    }

    /**
     * Will save a ENTITY, if the currentUser is allowed to save it.
     *
     * @param entity The ENTITY with all the properties of an object that should be created or updated.
     * @return The saved entity
     */
    public ENTITY save(final ENTITY entity) {
        final boolean newEntity = isNewEntity(entity);
        if (newEntity) {
            onBeforeSaveNewEntity(entity);
        } else {
            onBeforeSaveEntity(entity);
        }
        final ENTITY savedEntity = getRepository().save(entity);
        if (newEntity) {
            onAfterSaveNewEntity(savedEntity);
        } else {
            onAfterSaveEntity(savedEntity);
        }
        log.debug("Entity {} with ID# {} has been saved", entityClass.getCanonicalName(), savedEntity.getId());
        return savedEntity;
    }

    protected boolean isNewEntity(@NonNull final GenericID<ID> genericObject) {
        return genericObject.getId() == null;
    }

    // GET BY ID //
    public DTO getDTO(final Class<? extends AbstractAuditingEntity<ID>> entityClass, final ID id) {
        return convertToDto(get(id));
    }

    public ENTITY get(final ID id) {
        final Optional<ENTITY> entity = getRepository().findOneById(id);
        if (entity.isEmpty()) {
            log.error("Entity [{}] with id [{}] was not found!", entityClass, id);
            final ErrorDetail errorDetail = new ErrorDetail(
                    ErrorCode.NOT_FOUND,
                    List.of(new FieldErrorDto(entityClass.getCanonicalName(), "id", ErrorI18nKey.NOT_FOUND_KEY)));
            throw new ApplicationException(errorDetail);
        }
        return entity.get();
    }

    public void deleteDTO(final Class<? extends AbstractAuditingEntity<ID>> entityClass, final ID id) {
        delete(id);
    }

    public void delete(final ID id) {
        ENTITY entity = get(id);
        onBeforeDeleteEntity(entity);
        entity.setDeleted(System.currentTimeMillis() + "_" + System.nanoTime());
        getRepository().save(entity);
        onAfterDeleteEntity(entity);
    }

    // GET ALL //
    public Page<DTO> findAllDTO(
            final Integer page,
            final Integer size,
            Sort orderCriteria) {
        final PageRequest pageable = createPageRequest(page, size, orderCriteria);
        try {
            final Page<ENTITY> entities = getRepository().findAll(pageable);
            final List<DTO> details = entities.getContent().stream().map(this::convertToDto).collect(Collectors.toList());
            return new PageImpl<>(details, pageable, entities.getTotalElements());
        } catch (final Exception ex) {
            throw processException(ex, () -> {
                log.error("Error occurred while reading all the entities based on request [{}]!", pageable, ex);
                throw new ApplicationException(new ErrorDetail(ErrorCode.INTERNAL_ERROR));
            });
        }
    }

    protected PageRequest createPageRequest(final Integer pageNr, final Integer size, Sort orderCriteria) {
        final int page = pageNr != null ? pageNr : DEFAULT_PAGE_NUMBER;
        final int pageSize = size != null ? size : DEFAULT_PAGE_SIZE;

        final Sort ordering = orderCriteria == null ? getDefaultSort() : orderCriteria;
        if (ordering != null) {
            return PageRequest.of(page, pageSize, ordering);
        }

        return PageRequest.of(page, pageSize);
    }

    @ExcludeFromLoggingAspect
    public DTO convertToDto(final ENTITY entity) {
        if (entity == null)
            return null;
        DTO dto = getMapperService().convertToDTO(entity);
        customizeDto(dto, entity);
        return dto;
    }

    @ExcludeFromLoggingAspect
    public ENTITY convertToEntity(final DTO dto) {
        return getMapperService().convertToEntity(dto);
    }

    @SuppressWarnings({"unchecked"})
    private Mapper<ENTITY, DTO> getMapperService() {
        return mapperService.getMapperService(entityClass);
    }

    @ExcludeFromLoggingAspect
    public Class<? extends AbstractAuditingEntity<ID>> getEntityClass() {
        return entityClass;
    }
}
