package com.interview.service;

import com.interview.dto.PaginatedReadingListDto;
import com.interview.dto.ReadingListDto;
import com.interview.dto.ReadingListRequestDto;
import com.interview.entity.User;
import org.springframework.data.domain.Pageable;

public interface ReadingListService {
    PaginatedReadingListDto getAll(String keyword, String email, Pageable pageable);
    ReadingListDto findById(final long id);
    ReadingListDto save(final String username, final ReadingListRequestDto readingListRequestDto);
    void delete(final long id, final String username);
    PaginatedReadingListDto getAllShared(String keyword, Pageable pageable);
}
