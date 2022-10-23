package com.interview.application.rest.v1.userRelationship.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ListOfFriendsDto {
    @ApiModelProperty(value = "The list of friends", position = 1)
    private List<FriendDto> friends;
    @ApiModelProperty(value = "The page number", position = 2)
    private int pageNumber;
    @ApiModelProperty(value = "The page size", position = 3)
    private int numberOfElements;
    @ApiModelProperty(value = "true if there is a next page", position = 4)
    private boolean hasNext;
    @ApiModelProperty(value = "true if there is a previous page", position = 5)
    private boolean hasPrevious;
}
