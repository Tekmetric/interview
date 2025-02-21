package com.interview.keyword.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

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

import com.interview.exceptions.NotFoundException;
import com.interview.exceptions.UniqueConstraintViolationException;
import com.interview.keyword.model.Keyword;
import com.interview.keyword.repository.IKeywordRepository;

@ExtendWith(MockitoExtension.class)
public class KeywordServiceTest {
    @Mock
    private IKeywordRepository keywordRepository;

    @InjectMocks
    private KeywordService keywordService;

    private Keyword sampleKeyword;

    @BeforeEach
    void setUp() {
        sampleKeyword = new Keyword();
        sampleKeyword.setName("keyword");
    }

    @Test
    void testSaveKeyword() {
        when(keywordRepository.save(any(Keyword.class))).thenReturn(sampleKeyword);

        Keyword savedKeyword = keywordService.createKeyword(sampleKeyword);

        assertNotNull(savedKeyword);
        assertEquals("keyword", savedKeyword.getName());
        verify(keywordRepository, times(1)).save(sampleKeyword);
    }

    @Test
    void testSaveKeyword_ThrowsUniqueConstraintViolationException() {

        when(keywordRepository.findByName(anyString()))
                .thenReturn(Optional.of(sampleKeyword));

        UniqueConstraintViolationException exception = assertThrows(UniqueConstraintViolationException.class, () -> {
            keywordService.createKeyword(sampleKeyword);
        });

        assertEquals("Keyword already exists", exception.getMessage());
        verify(keywordRepository, times(1)).findByName("keyword");
    }

    @Test
    void testDeleteKeywordById() {
        when(keywordRepository.findById(1L)).thenReturn(Optional.of(sampleKeyword));

        keywordService.deleteKeywordById(1L);

        verify(keywordRepository, times(1)).delete(any(Keyword.class));
    }

    @Test
    void testDeleteKeywordById_ThrowsNotFoundException() {
        when(keywordRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            keywordService.deleteKeywordById(1L);
        });

        assertEquals("Keyword not found", exception.getMessage());
        verify(keywordRepository, never()).delete(any(Keyword.class));
    }

    @Test
    void testUpdateKeyword() {
        Keyword updatedKeyword = new Keyword();
        updatedKeyword.setName("keyword");

        when(keywordRepository.findById(1L)).thenReturn(Optional.of(sampleKeyword));
        when(keywordRepository.save(any(Keyword.class))).thenReturn(updatedKeyword);

        Keyword result = keywordService.updateKeyword(1L, updatedKeyword.getName());

        assertNotNull(result);
        assertEquals("keyword", result.getName());
        verify(keywordRepository, times(1)).save(sampleKeyword);
    }

    @Test
    void testUpdateKeyword_ThrowsNotFoundException() {
        when(keywordRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            keywordService.updateKeyword(1L, sampleKeyword.getName());
        });

        assertEquals("Keyword not found", exception.getMessage());
        verify(keywordRepository, never()).save(any(Keyword.class));
    }

    @Test
    void testGetKeywordById() {
        when(keywordRepository.findById(1L)).thenReturn(Optional.of(sampleKeyword));

        Keyword foundKeyword = keywordService.getKeywordById(1L);

        assertNotNull(foundKeyword);
        assertEquals("keyword", foundKeyword.getName());
    }

    @Test
    void testGetKeywordById_ThrowsNotFoundException() {
        when(keywordRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            keywordService.getKeywordById(1L);
        });

        assertEquals("Keyword not found", exception.getMessage());
    }

    @Test
    void testGetKeywords() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Keyword> keywordPage = new PageImpl<>(List.of(sampleKeyword));

        when(keywordRepository.findAll(pageable)).thenReturn(keywordPage);

        Page<Keyword> result = keywordService.getKeywords(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(keywordRepository, times(1)).findAll(pageable);
    }

}
