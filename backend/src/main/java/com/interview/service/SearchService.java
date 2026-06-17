package com.interview.service;

import com.interview.dto.SearchResultDto;
import com.interview.entity.SearchResult;
import com.interview.repository.SearchResultRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SearchService {

    private final SearchResultRepository searchResultRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public SearchService(SearchResultRepository searchResultRepository, ModelMapper modelMapper) {
        this.searchResultRepository = searchResultRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public Page<SearchResultDto> search(String query, Pageable pageable) {
        Page<SearchResult> results = searchResultRepository.findByNameContainingIgnoreCase(query, pageable);
        return results.map(result -> modelMapper.map(result, SearchResultDto.class));
    }

    @Transactional(readOnly = true)
    public Page<SearchResultDto> searchByType(String query, String entityType, Pageable pageable) {
        Page<SearchResult> results = searchResultRepository
                .findByEntityTypeAndNameContainingIgnoreCase(entityType, query, pageable);
        return results.map(result -> modelMapper.map(result, SearchResultDto.class));
    }

    @Transactional(readOnly = true)
    public Page<SearchResultDto> searchByArtist(String artistName, Pageable pageable) {
        Page<SearchResult> results = searchResultRepository
                .findByArtistNameContainingIgnoreCase(artistName, pageable);
        return results.map(result -> modelMapper.map(result, SearchResultDto.class));
    }
}
