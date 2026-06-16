package com.interview.application.port.in;

import com.interview.model.AutoPart;
import com.interview.model.AutoPartResponse;

public interface AutoPartUseCase {
    AutoPartResponse createAutoPart(AutoPart autoPart);

    void deleteAutoPart(String id);

    AutoPartResponse retrieveSingleAutoPart(String id);

    AutoPartResponse retrieveAllAutoParts();

    AutoPartResponse updateAutoPart(String id, AutoPart autoPart);
}
