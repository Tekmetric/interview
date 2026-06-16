package com.interview.application.service;

import com.interview.adapter.out.persistence.entities.AutoPartEntity;
import com.interview.application.port.in.AutoPartUseCase;
import com.interview.application.port.out.AutoPartPort;
import com.interview.common.mapper.AutoPartMapper;
import com.interview.common.validation.AutoPartValidator;
import com.interview.domain.AutoPartList;
import com.interview.model.AutoPart;
import com.interview.model.AutoPartResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AutoPartService implements AutoPartUseCase {
    private final AutoPartPort autoPartPort;
    private final AutoPartMapper mapper;
    private final AutoPartValidator validator;

    @Autowired
    public AutoPartService(AutoPartPort autoPartPort, AutoPartMapper mapper, AutoPartValidator validator) {
        this.autoPartPort = autoPartPort;
        this.mapper = mapper;
        this.validator = validator;
    }

    @Override
    public AutoPartResponse createAutoPart(AutoPart autoPart) {
        //Input request body validation happens at the time of the request, requirements set in OpenAPI spec
        AutoPartEntity autoPartEntity = mapper.mapToEntity(autoPart);

        AutoPartEntity savedEntity = autoPartPort.createAutoPart(autoPartEntity);

        return mapper.mapToDto(savedEntity);
    }

    @Override
    public void deleteAutoPart(String id) {
        UUID validatedId = validator.validateAutoPartId(id);

        autoPartPort.deleteAutoPart(validatedId);
    }

    @Override
    public AutoPartResponse retrieveSingleAutoPart(String id) {
        UUID validatedId = validator.validateAutoPartId(id);

        AutoPartEntity autoPartEntity = autoPartPort.retrieveSingleAutoPart(validatedId);

        return mapper.mapToDto(autoPartEntity);
    }

    @Override
    public AutoPartResponse retrieveAllAutoParts() {
        List<AutoPartEntity> autoPartEntityList = autoPartPort.retrieveAllAutoParts();

        List<AutoPart> dtoList = autoPartEntityList.stream()
                .map(mapper::mapToDto)
                .toList();

        return new AutoPartList(dtoList);
    }

    @Override
    public AutoPartResponse updateAutoPart(String id, AutoPart autoPart) {
        //Input request body validation happens at the time of the request, requirements set in OpenAPI spec
        // Still need to validate UUID format of id path variable
        UUID validatedId = validator.validateAutoPartId(id);

        AutoPartEntity existingAutoPart = autoPartPort.retrieveSingleAutoPart(validatedId);

        AutoPartEntity autoPartEntity = mapper.mapToEntity(autoPart);
        existingAutoPart = mapper.mapUpdatedAutoPartEntityFields(autoPartEntity, existingAutoPart);

        AutoPartEntity updatedEntity = autoPartPort.updateAutoPart(existingAutoPart);

        return mapper.mapToDto(updatedEntity);
    }
}
