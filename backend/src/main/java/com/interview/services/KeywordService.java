package com.interview.services;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.interview.models.Keyword;
import com.interview.repositories.IKeywordRepository;

@Service
public class KeywordService {

    IKeywordRepository keywordRepository;

    public KeywordService(IKeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }

    @Transactional
    public Keyword saveKeyword(Keyword keyword) {
        return this.keywordRepository.save(keyword);
    }

    @Transactional
    public void deleteKeywordById(long id) {
        Keyword keyword = getKeywordById(id);
        this.keywordRepository.delete(keyword);
    }

    @Transactional
    public Keyword updateKeyword(long id, String keyword) {
        Keyword keywordToUpdate = this.keywordRepository.findById(id).get();
        keywordToUpdate.setName(keyword);

        return this.keywordRepository.save(keywordToUpdate);
    }

    public Keyword getKeywordById(long id) {
        return this.keywordRepository.findById(id).orElseThrow(() -> new RuntimeException("Keyword not found"));
    }

    public Page<Keyword> getKeywords(Pageable pageable) {
        return this.keywordRepository.findAll(pageable);
    }

}
