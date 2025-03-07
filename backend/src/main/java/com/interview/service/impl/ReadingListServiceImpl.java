package com.interview.service.impl;

import com.interview.dto.PaginatedReadingListDto;
import com.interview.dto.ReadingListDto;
import com.interview.dto.ReadingListRequestDto;
import com.interview.entity.ReadingList;
import com.interview.entity.User;
import com.interview.repository.ReadingListRepository;
import com.interview.repository.UserRepository;
import com.interview.service.ReadingListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReadingListServiceImpl implements ReadingListService {

    private static final Logger logger = LoggerFactory.getLogger(ReadingListServiceImpl.class);

    private final ReadingListRepository readingListRepository;

    private final UserRepository userRepository;

    public ReadingListServiceImpl(ReadingListRepository readingListRepository,
                                  UserRepository userRepository) {
        this.readingListRepository = readingListRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PaginatedReadingListDto getAll(String keyword, String email, Pageable pageable) {
        logger.debug("Finding reading lists of user {} for keyword {} and pageable {}.",
                email,
                keyword,
                pageable);


        String keywordParam = StringUtils.hasLength(keyword) ? keyword.toUpperCase() : null;
        final Page<ReadingList> pageOfReadingLists =
                readingListRepository.findAllByKeywordAndOwnerEmail(keywordParam, email, pageable);

        return new PaginatedReadingListDto(pageable.getPageNumber(),
                pageOfReadingLists.getTotalPages(),
                pageOfReadingLists.getTotalElements(),
                pageOfReadingLists.getContent()
                        .stream()
                        .map(ReadingListDto::new)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public ReadingListDto findById(long id) {
        logger.debug("Finding reading list by id {}", id);
        return readingListRepository.findById(id).map(ReadingListDto::new).orElse(null);
    }

    @Override
    public ReadingListDto save(final String username, final ReadingListRequestDto readingListRequestDto) {
        logger.debug("Saving reading list {} of {}", readingListRequestDto, username);

        User user = userRepository.findByEmail(username).get();
        ReadingListDto readingListDto = readingListRequestDto.getReadingListDto(user.getId());
        ReadingList readingList = new ReadingList(readingListDto);
        readingListRepository.save(readingList);
        return new ReadingListDto(readingList);
    }

    @Override
    public void delete(long id, String username) {
        logger.debug("Deleting reading list by id {}", id);
        readingListRepository.deleteById(id);
    }

    @Override
    public PaginatedReadingListDto getAllShared(String keyword, Pageable pageable) {
        logger.debug("Finding reading lists for keyword {}, pageable {}",
                keyword,
                pageable);

        String keywordParam = StringUtils.hasLength(keyword) ? keyword.toUpperCase() : null;
        final Page<ReadingList> pageOfReadingLists =
                readingListRepository.findAllBySharedAndKeyword(keywordParam, true, pageable);

        return new PaginatedReadingListDto(pageable.getPageNumber(),
                pageOfReadingLists.getTotalPages(),
                pageOfReadingLists.getTotalElements(),
                pageOfReadingLists.getContent()
                        .stream()
                        .map(ReadingListDto::new)
                        .collect(Collectors.toList())
        );

    }
}
