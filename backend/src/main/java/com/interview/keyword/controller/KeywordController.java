package com.interview.keyword.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interview.keyword.dto.KeywordDTO;
import com.interview.keyword.model.Keyword;
import com.interview.keyword.service.KeywordService;
import com.interview.shared.util.ConvertUtil;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/keyword")
public class KeywordController {

    KeywordService keywordService;

    public KeywordController(KeywordService keywordService) {
        this.keywordService = keywordService;
    }

    /**
     * Get all keywords, paged
     * 
     * @param pageable
     * @return
     */
    @GetMapping
    public ResponseEntity<Page<KeywordDTO>> getKeywords(final Pageable pageable) {
        return ResponseEntity.ok(keywordService.getKeywords(pageable)
                .map(keyword -> ConvertUtil.convertToDTO(keyword, KeywordDTO.class)));
    }

    /**
     * Get keyword by id
     * 
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<KeywordDTO> getKeywordById(@PathVariable("id") long id) {
        Keyword keyword = keywordService.getKeywordById(id);
        return ResponseEntity.ok(ConvertUtil.convertToDTO(keyword, KeywordDTO.class));
    }

    /**
     * Create keyword
     * 
     * @param keywordDTO
     * @return
     */
    @PostMapping
    public ResponseEntity<KeywordDTO> createKeyword(@Valid @RequestBody KeywordDTO keywordDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ConvertUtil.convertToDTO(keywordService.createKeyword(new Keyword(keywordDTO)),
                        KeywordDTO.class));
    }

    /**
     * Delete keyword by id
     * 
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKeywordById(@PathVariable("id") long id) {
        keywordService.deleteKeywordById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update keyword
     * 
     * @param id
     * @param keyword
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<KeywordDTO> updateKeyword(@PathVariable("id") long id,
            @Valid @RequestBody KeywordDTO keyword) {

        return ResponseEntity
                .ok(ConvertUtil.convertToDTO(keywordService.updateKeyword(id, keyword.getName()), KeywordDTO.class));
    }
}
