package com.interview.dto;

import com.interview.entity.ReadingList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadingListDto implements Serializable {
    private Long id;
    private String name;
    private UserDto owner;
    private LocalDateTime lastUpdate = LocalDateTime.now();
    private boolean shared;
    private List<BookDto> books;

    public ReadingListDto(ReadingList readingList) {
        this.id = readingList.getId();
        this.name = readingList.getName();
        this.owner = new UserDto(readingList.getOwner());
        this.lastUpdate = readingList.getLastUpdate();
        this.shared = readingList.isShared();
        this.books = readingList.getBookList().stream().map(BookDto::new).collect(Collectors.toList());
    }
}
