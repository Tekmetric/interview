package com.interview.application.rest.v1.userRelationship.dto;

import com.interview.application.rest.v1.common.dto.AbstractAuditingDto;
import com.interview.application.rest.v1.user.dto.UserDto;
import com.interview.domain.model.enums.UserRelationshipState;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRelationshipDto extends AbstractAuditingDto<Long> {

    @ApiModelProperty(value = "The user relationship id", position = 1)
    private Long id;
    /**
     * The User who initiated the friend request
     */
    @ApiModelProperty(value = "The user that creates the relation", position = 2, required = true)
    @NotNull
    private UserDto sender;
    /**
     * The User who received the friend request
     */
    @NotNull
    @ApiModelProperty(value = "The user that receives the relation", position = 3, required = true)
    private UserDto receiver;

    @ApiModelProperty(value = "The relationship state.", position = 4)
    @Enumerated(EnumType.STRING)
    private UserRelationshipState state;

    @Override
    public String toString() {
        return "UserRelationshipDto{" +
                "id=" + id + '\'' +
                ", sender=" + prettyPrintDTO(sender) + '\'' +
                ", receiver=" + prettyPrintDTO(receiver) + '\'' +
                ", state=" + state +
                '}';
    }
}
