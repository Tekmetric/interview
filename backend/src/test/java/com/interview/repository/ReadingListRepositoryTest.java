package com.interview.repository;

import com.interview.entity.ReadingList;
import com.interview.entity.User;
import com.interview.testutil.CommonTestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReadingListRepositoryTest {

    @Autowired
    private ReadingListRepository readingListRepository;

    @Autowired
    private UserRepository userRepository;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setFirstName(CommonTestConstants.FIRST_NAME_1);
        user.setLastName(CommonTestConstants.LAST_NAME_1);
        user.setEmail(CommonTestConstants.EMAIL_1);
        user.setPassword(CommonTestConstants.PASSWORD);
        user.setAdmin(false);
        userRepository.save(user);

        ReadingList readingList1 = new ReadingList();
        readingList1.setName(CommonTestConstants.NAME_1);
        readingList1.setOwner(user);
        readingList1.setShared(true);
        readingList1.setLastUpdate(CommonTestConstants.SHARED_DATE);
        readingListRepository.save(readingList1);

        ReadingList readingList2 = new ReadingList();
        readingList2.setName(CommonTestConstants.NAME_2);
        readingList2.setOwner(user);
        readingList2.setShared(false);
        readingList2.setLastUpdate(CommonTestConstants.SHARED_DATE);
        readingListRepository.save(readingList2);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void testFindAllBySharedAndKeyword() {
        String keyword = "name";

        Page<ReadingList> result =
                readingListRepository.findAllBySharedAndKeyword(keyword.toUpperCase(), true, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).getName().equals(CommonTestConstants.NAME_1));
    }

    @Test
    void testFindAllBySharedAndKeywordEmptyResult() {
        String keyword = "NonExistent";

        Page<ReadingList> result = readingListRepository.findAllBySharedAndKeyword(keyword, true, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void testFindAllByKeywordAndOwnerEmail() {
        String keyword = CommonTestConstants.NAME_1;

        Page<ReadingList> result = readingListRepository.findAllByKeywordAndOwnerEmail(
                keyword.toUpperCase(), CommonTestConstants.EMAIL_1, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).getName().equals(CommonTestConstants.NAME_1));
    }

    @Test
    void testFindAllByKeywordAndOwnerEmailEmptyResult() {
        String keyword = "NonExistent";

        Page<ReadingList> result = readingListRepository.findAllByKeywordAndOwnerEmail(
                keyword, CommonTestConstants.EMAIL_1, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }
}
