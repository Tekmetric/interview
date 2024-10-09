package com.interview.service;

import com.interview.dto.CatDTO;
import com.interview.dto.CreateCatDTO;
import com.interview.exception.CatNotFoundException;

import java.util.List;

public interface CatService {
    CatDTO createCat(CreateCatDTO createCatDTO);
    CatDTO getCatById(Long id) throws CatNotFoundException;
    List<CatDTO> getAllCats();
    void updateCat(Long catId, CatDTO cat);
    void deleteCatById(Long id) throws CatNotFoundException;
}
