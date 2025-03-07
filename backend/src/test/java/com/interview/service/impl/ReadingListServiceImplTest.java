package com.interview.service.impl;

import com.interview.dto.PaginatedReadingListDto;
import com.interview.dto.ReadingListDto;
import com.interview.dto.ReadingListRequestDto;
import com.interview.dto.UserDto;
import com.interview.entity.ReadingList;
import com.interview.entity.User;
import com.interview.repository.ReadingListRepository;
import com.interview.repository.UserRepository;
import com.interview.testutil.CommonTestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadingListServiceImplTest {

    @Mock
    private ReadingListRepository readingListRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReadingListServiceImpl readingListService;

    private ReadingList readingList;
    private ReadingListRequestDto readingListRequestDto;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        User user = new User(
                CommonTestConstants.ID_1,
                CommonTestConstants.FIRST_NAME_1,
                CommonTestConstants.LAST_NAME_1,
                CommonTestConstants.EMAIL_1,
                CommonTestConstants.ENCODED_PASSWORD,
                false);

        readingList = new ReadingList(
                CommonTestConstants.ID_1,
                CommonTestConstants.NAME_1,
                user,
                CommonTestConstants.SHARED_DATE,
                false,
                new ArrayList<>());

        readingListRequestDto = new ReadingListRequestDto(
                CommonTestConstants.ID_1,
                CommonTestConstants.NAME_1,
                true,
                new ArrayList<>()
        );

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void testGetAll() {
        Page<ReadingList> page = new PageImpl<>(Collections.singletonList(readingList));
        when(readingListRepository.findAllByKeywordAndOwnerEmail(any(), anyString(), any())).thenReturn(page);

        PaginatedReadingListDto result = readingListService.getAll("first", CommonTestConstants.EMAIL_1, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalItems());
        verify(readingListRepository, times(1)).findAllByKeywordAndOwnerEmail(any(), anyString(), any());
    }

    @Test
    void testFindById() {
        when(readingListRepository.findById(anyLong())).thenReturn(Optional.of(readingList));

        ReadingListDto result = readingListService.findById(CommonTestConstants.ID_1);

        assertNotNull(result);
        assertEquals(CommonTestConstants.ID_1, result.getId());
        verify(readingListRepository, times(1)).findById(anyLong());
    }

    @Test
    void testFindByIdNotFound() {
        when(readingListRepository.findById(anyLong())).thenReturn(Optional.empty());

        ReadingListDto result = readingListService.findById(2L);

        assertNull(result);
        verify(readingListRepository, times(1)).findById(anyLong());
    }

    @Test
    void testSave() {
        when(readingListRepository.save(any())).thenReturn(readingList);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new User()));

        ReadingListDto result = readingListService.save(CommonTestConstants.EMAIL_1, readingListRequestDto);

        assertNotNull(result);
        assertEquals(CommonTestConstants.ID_1, result.getId());
        verify(readingListRepository, times(1)).save(any());
    }

    @Test
    void testDelete() {
        doNothing().when(readingListRepository).deleteById(anyLong());

        readingListService.delete(CommonTestConstants.ID_1, CommonTestConstants.EMAIL_1);

        verify(readingListRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void testGetAllShared() {
        Page<ReadingList> page = new PageImpl<>(Collections.singletonList(readingList));
        when(readingListRepository.findAllBySharedAndKeyword(any(), eq(true), any())).thenReturn(page);

        PaginatedReadingListDto result = readingListService.getAllShared("name", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalItems());
        verify(readingListRepository, times(1)).findAllBySharedAndKeyword(any(), eq(true), any());
    }
}
