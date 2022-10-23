package com.interview.domain.model.common;

/**
 * Base interface for model id.
 * @param <ID> the model id.
 */
public interface GenericID<ID> {
    ID getId();
    void setId(ID id);
}
