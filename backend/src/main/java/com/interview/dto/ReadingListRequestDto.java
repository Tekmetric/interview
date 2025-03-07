package com.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadingListRequestDto implements Serializable {
    private Long id;

    @NotBlank(message = "{error.required.readingList.name}")
    private String name;

    private boolean shared;

    private List<Long> bookIds;

    public ReadingListDto getReadingListDto(long userId) {
        ReadingListDto readingListDto = new ReadingListDto();
        readingListDto.setId(id);
        readingListDto.setName(name);
        readingListDto.setShared(shared);
        readingListDto.setBooks(bookIds.stream().map(BookDto::new).collect(Collectors.toList()));
        readingListDto.setOwner(new UserDto(userId));
        return readingListDto;
    }
}
