package com.interview.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.interview.dto.KeywordDTO;
import com.interview.models.Keyword;

public class KeywordTest {
    @Test
    void testKeywordConstructorFromDTO() {
        KeywordDTO keywordDTO = new KeywordDTO();
        keywordDTO.setName("Action");
        Keyword keyword = new Keyword(keywordDTO);
        assertNotNull(keyword);
        assertEquals("Action", keyword.getName());
    }

    @Test
    void testSettersAndGetters() {
        Keyword keyword = new Keyword();
        String name = "Adventure";
        keyword.setName(name);
        assertEquals(name, keyword.getName());
    }

    @Test
    void testDefaultConstructor() {
        Keyword keyword = new Keyword();
        assertNotNull(keyword);
        assertEquals(0, keyword.getId());
        assertNull(keyword.getName());
    }
}
