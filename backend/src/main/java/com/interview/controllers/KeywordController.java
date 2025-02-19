package com.interview.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.interview.dtos.KeywordDTO;
import com.interview.models.Keyword;
import com.interview.services.KeywordService;

@Controller
@RequestMapping("/api/keyword")
public class KeywordController {

    KeywordService keywordService;

    public KeywordController(KeywordService keywordService) {
        this.keywordService = keywordService;
    }

    @GetMapping
    public ResponseEntity<Page<KeywordDTO>> getKeywords(final Pageable pageable) {
        return ResponseEntity.ok(keywordService.getKeywords(pageable).map(this::convertToDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<KeywordDTO> getKeywordById(@PathVariable("id") long id) {
        Keyword keyword = keywordService.getKeywordById(id);

        if (keyword == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(convertToDTO(keyword));
    }

    @PostMapping
    public ResponseEntity<KeywordDTO> saveMovie(@RequestBody KeywordDTO director) {
        return ResponseEntity.ok(convertToDTO(keywordService.saveKeyword(new Keyword(director))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActorById(@PathVariable("id") long id) {
        keywordService.deleteKeywordById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<KeywordDTO> updateMovie(@PathVariable("id") long id, final KeywordDTO keyword) {
        return ResponseEntity.ok(convertToDTO(keywordService.updateKeyword(id, keyword.getName())));
    }

    private KeywordDTO convertToDTO(Keyword keyword) {
        return new KeywordDTO(keyword);
    }
}
