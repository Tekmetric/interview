package com.interview.application.rest.v1.userRelationship.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendDto {
    @ApiModelProperty(value = "The id of the user", position = 1)
    private Long id;
    @ApiModelProperty(value = "The user relationship id", position = 2)
    private Long userRelationshipId;
    @ApiModelProperty(value = "The user display name", position = 3)
    private String displayName;
    @ApiModelProperty(value = "The user initials", position = 4)
    private String initials;
}
