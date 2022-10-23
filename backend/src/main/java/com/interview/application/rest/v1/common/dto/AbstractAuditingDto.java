package com.interview.application.rest.v1.common.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.interview.application.configuration.jakson.View;
import com.interview.domain.model.common.GenericID;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Base abstract class for entities which will hold definitions for created by,
 * last modified by and created date and last modified date.
 */
@Getter
@Setter
@ToString
public abstract class AbstractAuditingDto<T> implements GenericID<T> {

    @ApiModelProperty(value = "The user that created the entity", position = 1)
    @JsonView(View.Internal.class)
    private String createdBy;
    @ApiModelProperty(value = "The date of creation of the entity", position = 2)
    private LocalDateTime createdDate = LocalDateTime.now(ZoneId.of("UTC"));
    @JsonView(View.Internal.class)
    @ApiModelProperty(value = "The latest user that modified the entity", position = 3)
    private String lastModifiedBy;
    @JsonView(View.Internal.class)
    @ApiModelProperty(value = "The date of latest modification of the entity", position = 4)
    private LocalDateTime lastModifiedDate = LocalDateTime.now(ZoneId.of("UTC"));
    @ApiModelProperty(value = "Is deleted flag", position = 5)
    @JsonView(View.Internal.class)
    private String deleted;

    protected String prettyPrintDTO(AbstractAuditingDto<?> object) {
        if (object == null) {
            return "null";
        } else {
            return String.valueOf(object.getId());
        }
    }
}
