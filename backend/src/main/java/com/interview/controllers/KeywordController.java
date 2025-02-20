package com.interview.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interview.dto.KeywordDTO;
import com.interview.models.Keyword;
import com.interview.services.KeywordService;
import com.interview.util.ConvertUtil;

@RestController
@RequestMapping("/api/keyword")
public class KeywordController {

    KeywordService keywordService;

    public KeywordController(KeywordService keywordService) {
        this.keywordService = keywordService;
    }

    @GetMapping
    public ResponseEntity<Page<KeywordDTO>> getKeywords(final Pageable pageable) {
        return ResponseEntity.ok(keywordService.getKeywords(pageable)
                .map(keyword -> ConvertUtil.convertToDTO(keyword, KeywordDTO.class)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<KeywordDTO> getKeywordById(@PathVariable("id") long id) {
        Keyword keyword = keywordService.getKeywordById(id);
        return ResponseEntity.ok(ConvertUtil.convertToDTO(keyword, KeywordDTO.class));
    }

    @PostMapping
    public ResponseEntity<KeywordDTO> saveMovie(@RequestBody KeywordDTO director) {
        return ResponseEntity
                .ok(ConvertUtil.convertToDTO(keywordService.saveKeyword(new Keyword(director)), KeywordDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActorById(@PathVariable("id") long id) {
        keywordService.deleteKeywordById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<KeywordDTO> updateMovie(@PathVariable("id") long id, final KeywordDTO keyword) {
        return ResponseEntity
                .ok(ConvertUtil.convertToDTO(keywordService.updateKeyword(id, keyword.getName()), KeywordDTO.class));
    }
}
