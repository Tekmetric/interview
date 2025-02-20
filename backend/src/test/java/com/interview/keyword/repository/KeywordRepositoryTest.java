package com.interview.keyword.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.interview.actor.model.Actor;
import com.interview.keyword.model.Keyword;

import jakarta.transaction.Transactional;

@DataJpaTest
public class KeywordRepositoryTest {

    @Autowired
    private IKeywordRepository keywordRepository;

    @Test
    @Transactional
    void testSaveAndFindById() {
        Keyword keyword = new Keyword();
        keyword.setName("New");

        Keyword savedKeyword = keywordRepository.save(keyword);
        Keyword foundKeyword = keywordRepository.findById(savedKeyword.getId()).orElse(null);

        assertNotNull(foundKeyword);
        assertEquals("New", foundKeyword.getName());
    }

    @Test
    @Transactional
    void testUpdate() {
        Keyword keyword = new Keyword();
        keyword.setName("Update");

        Keyword savedKeyword = keywordRepository.save(keyword);
        savedKeyword.setName("Updated");
        keywordRepository.save(savedKeyword);

        Keyword foundKeyword = keywordRepository.findById(savedKeyword.getId()).orElse(null);

        assertNotNull(foundKeyword);
        assertEquals("Updated", foundKeyword.getName());
    }

    @Test
    @Transactional
    void testDelete() {
        Keyword keyword = new Keyword();
        keyword.setName("Delete");

        Keyword savedKeyword = keywordRepository.save(keyword);
        keywordRepository.deleteById(savedKeyword.getId());
    }

    @Test
    @Transactional
    void testFindByName() {
        Keyword keyword = new Keyword();
        keyword.setName("keyword");

        keywordRepository.save(keyword);

        Keyword foundKeyword = keywordRepository.findByName("keyword").orElse(null);

        assertNotNull(foundKeyword);
        assertEquals("keyword", foundKeyword.getName());

    }
}
