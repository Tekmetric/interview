package com.interview.service;

import com.interview.dto.CatDTO;
import com.interview.dto.CreateCatDTO;
import com.interview.exception.CatNotFoundException;
import com.interview.model.Cat;
import com.interview.repository.CatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatServiceImpl implements CatService {

    @Autowired
    private CatRepository catRepository;

    @Override
    public CatDTO createCat(CreateCatDTO createCatDTO) {
        Cat newCat = catRepository.saveAndFlush(createCatDTO.toNewCat());

        return CatDTO.fromCat(newCat);
    }

    @Override
    public CatDTO getCatById(Long id) throws CatNotFoundException {
        return catRepository.findById(id).map(CatDTO::fromCat)
                .orElseThrow(() -> new CatNotFoundException(id));
    }

    @Override
    public List<CatDTO> getAllCats() {
        return catRepository.findAll().stream().map(CatDTO::fromCat)
                .collect(Collectors.toList());
    }

    @Override
    public void updateCat(Long id, CatDTO cat) {
        Cat updatedCat = cat.toUpdatedCat(id);

        catRepository.saveAndFlush(updatedCat);
    }

    public void deleteCatById(Long id) throws CatNotFoundException {
        Long foundCatId = catRepository.findById(id).map(Cat::getId)
                .orElseThrow(() -> new CatNotFoundException(id));

        catRepository.deleteById(foundCatId);
    }
}
