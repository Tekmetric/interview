package com.interview.services;

import jakarta.transaction.Transactional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.interview.exceptions.NotFoundException;
import com.interview.exceptions.UniqueConstraintViolationException;
import com.interview.models.Keyword;
import com.interview.repositories.IKeywordRepository;

@Service
public class KeywordService {

    IKeywordRepository keywordRepository;

    public KeywordService(IKeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }

    @Transactional
    @CacheEvict(value = "keywordList", allEntries = true)
    public Keyword saveKeyword(Keyword keyword) {
        try {
            return this.keywordRepository.save(keyword);
        } catch (DataIntegrityViolationException e) {
            throw new UniqueConstraintViolationException("Keyword already exists");
        }
    }

    @Transactional
    @CacheEvict(value = "keywordList", allEntries = true)
    public void deleteKeywordById(long id) {
        Keyword keyword = getKeywordById(id);
        this.keywordRepository.delete(keyword);
    }

    @Transactional
    @CacheEvict(value = "keywordList", allEntries = true)
    public Keyword updateKeyword(long id, String keyword) {
        Keyword keywordToUpdate = getKeywordById(id);
        keywordToUpdate.setName(keyword);

        return this.keywordRepository.save(keywordToUpdate);
    }

    public Keyword getKeywordById(long id) {
        return this.keywordRepository.findById(id).orElseThrow(() -> new NotFoundException("Keyword not found"));
    }

    @Cacheable(value = "keywordList", key = "'page:' + #pageable.pageNumber + '- size:' + #pageable.pageSize")
    public Page<Keyword> getKeywords(Pageable pageable) {
        return this.keywordRepository.findAll(pageable);
    }

}
