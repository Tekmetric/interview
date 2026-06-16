package com.interview.application.port.out;

import com.interview.adapter.out.persistence.entities.AutoPartEntity;

import java.util.List;
import java.util.UUID;

public interface AutoPartPort {
    AutoPartEntity createAutoPart(AutoPartEntity autoPartEntity);

    void deleteAutoPart(UUID id);

    AutoPartEntity retrieveSingleAutoPart(UUID id);

    List<AutoPartEntity> retrieveAllAutoParts();

    AutoPartEntity updateAutoPart(AutoPartEntity autoPartEntity);
}
