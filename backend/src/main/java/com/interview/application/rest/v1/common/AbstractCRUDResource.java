/**
 * Base CRUD spring boot resource. Contains the base CRUD operations.
 * <p/>
 * Under the hood it uses the {@link com.interview.domain.service.common.AbstractCRUDService} spring service for
 * business logic.
 * <p/>
 * By default, the CRUD operation are not activated. To activate the CRUD operations you need to manually add the
 * following annotations:
 * {@link com.interview.application.rest.v1.common.annotations.CreateOperationIsSupported}
 * {@link com.interview.application.rest.v1.common.annotations.DeleteOperationIsSupported}
 * {@link com.interview.application.rest.v1.common.annotations.GetByIdOperationIsSupported}
 * {@link com.interview.application.rest.v1.common.annotations.GetAllOperationIsSupported}
 * {@link com.interview.application.rest.v1.common.annotations.UpdateOperationIsSupported}
 */
package com.interview.application.rest.v1.common;

import com.interview.application.rest.v1.common.annotations.*;
import com.interview.application.rest.v1.common.dto.AbstractAuditingDto;
import com.interview.domain.exception.ApplicationException;
import com.interview.domain.exception.ErrorCode;
import com.interview.domain.exception.ErrorDetail;
import com.interview.domain.model.common.AbstractAuditingEntity;
import com.interview.domain.service.common.AbstractCRUDService;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public abstract class AbstractCRUDResource<
        ENTITY extends AbstractAuditingEntity<ID>,
        ID extends Serializable & Comparable<ID>,
        DTO extends AbstractAuditingDto<ID>> extends AbstractResource {

    /**
     * @return the underlying service that we use
     */
    protected abstract AbstractCRUDService<ENTITY, ID, DTO> getService();

    /**
     * Generic rest api method for create a new {@link ENTITY} object
     *
     * @param body request data transfer object used to create the new entity
     * @return {@link HttpStatus#CREATED} with the new entity in case everything is ok
     */
    @Operation(summary = "Creates a new entity")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Entity was created."),
                    @ApiResponse(responseCode = "405", description = "Method execution is not allowed."),
                    @ApiResponse(responseCode = "400", description = "The request is invalid.")
            }
    )
    @PostMapping
    public ResponseEntity<DTO> createEntity(
            @ApiParam(required = true, value = "the new entity")
            final @Valid @RequestBody DTO body)
            throws Exception {
        if (!this.getClass().isAnnotationPresent(CreateOperationIsSupported.class)) {
            log.error("Create operation is not permitted for this url [{}].", getPathURL());
            throw new ApplicationException(new ErrorDetail(ErrorCode.METHOD_NOT_ALLOWED));
        }

        if (body.getId() != null) {
            log.error("This is a create entity operation. Please don't set the entity id!!!");
            throw new ApplicationException(new ErrorDetail(ErrorCode.BAD_REQUEST));
        }

        DTO dto = getService().saveDTO(body);

        return ResponseEntity.created(new URI(getPathURL() + "/" + dto.getId())).body(dto);
    }

    /**
     * @param body the new values of the updated entity
     * @return the updated value of the entity
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing entity")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Entity was updated."),
                    @ApiResponse(responseCode = "405", description = "Method execution is not allowed."),
                    @ApiResponse(responseCode = "400", description = "The request is invalid."),
                    @ApiResponse(responseCode = "404", description = "The entity to be updated is not found.")
            }
    )
    public ResponseEntity<DTO> updateEntity(
            @ApiParam(required = true, value = "the entity id")
            final @PathVariable("id") ID id,
            @ApiParam(required = true, value = "the updated entity")
            final @Valid @RequestBody DTO body) {
        if (!this.getClass().isAnnotationPresent(UpdateOperationIsSupported.class)) {
            log.error("Update operation is not permitted for this url [{}].", getPathURL());
            throw new ApplicationException(new ErrorDetail(ErrorCode.METHOD_NOT_ALLOWED));
        }

        return ResponseEntity.ok(getService().updateDTO(id, body));
    }

    /**
     * Generic rest api method for getting specific {@link DTO} based on current person
     *
     * @param id id of the entity that we want to return
     * @return the entity
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get an existing entity by id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Entity was retrieved."),
                    @ApiResponse(responseCode = "405", description = "Method execution is not allowed."),
                    @ApiResponse(responseCode = "400", description = "The request is invalid."),
                    @ApiResponse(responseCode = "404", description = "The entity is not found.")
            }
    )
    public ResponseEntity<DTO> findEntityById(
            @ApiParam(required = true, value = "the entity id")
            final @PathVariable("id") ID id) {
        if (!this.getClass().isAnnotationPresent(GetByIdOperationIsSupported.class)) {
            log.error("Get by id is not permitted for this url [{}].", getPathURL());
            throw new ApplicationException(new ErrorDetail(ErrorCode.METHOD_NOT_ALLOWED));
        }

        return ResponseEntity.ok(getService().getDTO(getService().getEntityClass(), id));
    }

    /**
     * Generic rest api method for getting all specific {@link ENTITY} based on current person
     *
     * @return {@link HttpStatus#OK} with a collection of entities from database
     */
    @GetMapping
    @Operation(summary = "Get all entities")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Entities were retrieved."),
                    @ApiResponse(responseCode = "405", description = "Method execution is not allowed."),
                    @ApiResponse(responseCode = "400", description = "The request is invalid."),
            }
    )
    public ResponseEntity<List<DTO>> getAllEntities(
            @ApiParam(value = "the page number")
            final @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(value = "the page size")
            final @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(value = "the sort by value")
            final @RequestParam(value = "sortBy", required = false) String sortBy,
            @ApiParam(value = "the sort direction")
            final @RequestParam(value = "sortOrder", required = false, defaultValue = "ASC") String sortDir) {
        if (!this.getClass().isAnnotationPresent(GetAllOperationIsSupported.class)) {
            log.error("Get all operation is not permitted for this url [{}].", getPathURL());
            throw new ApplicationException(new ErrorDetail(ErrorCode.METHOD_NOT_ALLOWED));
        }
        Sort sortCriteria = sortBy == null ? null : Sort.by(Sort.Direction.valueOf(sortDir), sortBy);
        final Page<DTO> response = getService().findAllDTO(page, size, sortCriteria);

        final HttpHeaders headers = new HttpHeaders();
        headers.add(PathConstants.TOTAL_COUNT, String.valueOf(response.getTotalElements()));

        return new ResponseEntity<>(new LinkedList<>(response.getContent()), headers, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an existing entity")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Entity was logically deleted."),
                    @ApiResponse(responseCode = "405", description = "Method execution is not allowed."),
                    @ApiResponse(responseCode = "400", description = "The request is invalid."),
                    @ApiResponse(responseCode = "404", description = "The entity to be deleted is not found.")
            }
    )
    public ResponseEntity<?> deleteEntityById(
            @ApiParam(required = true, value = "the entity id")
            final @PathVariable("id") ID id) {
        if (!this.getClass().isAnnotationPresent(DeleteOperationIsSupported.class)) {
            log.error("Delete by id is not permitted for this url [{}].", getPathURL());
            throw new ApplicationException(new ErrorDetail(ErrorCode.METHOD_NOT_ALLOWED));
        }
        getService().deleteDTO(getService().getEntityClass(), id);
        return ResponseEntity.noContent().build();
    }

    private String getPathURL() {
        if (this.getClass().isAnnotationPresent(RequestMapping.class)) {
            RequestMapping requestMapping = this.getClass().getAnnotation(RequestMapping.class);
            return requestMapping.value()[0];
        }
        log.error("Could not fount RequestMapping annotation for this request!!! We will not process it");
        throw new ApplicationException(new ErrorDetail(ErrorCode.INTERNAL_ERROR));
    }
}
