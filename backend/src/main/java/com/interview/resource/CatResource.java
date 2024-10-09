package com.interview.resource;

import com.interview.dto.CatDTO;
import com.interview.dto.CreateCatDTO;
import com.interview.service.CatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/cats")
public class CatResource {

    @Autowired
    private CatService catService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CatDTO> createCat(@RequestBody CreateCatDTO createCatDTO) {
        CatDTO newCat = catService.createCat(createCatDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newCat);
    }

    @GetMapping(value = "/{catId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CatDTO> getCatById(@PathVariable Long catId) {
        CatDTO foundCat = catService.getCatById(catId);

        return ResponseEntity.status(HttpStatus.OK).body(foundCat);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CatDTO>> getAllCats() {
        List<CatDTO> foundCats = catService.getAllCats();

        return ResponseEntity.status(HttpStatus.OK).body(foundCats);
    }

    @PutMapping(value = "/{catId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateCat(@PathVariable Long catId, @RequestBody CatDTO cat) {
        catService.updateCat(catId, cat);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping(value = "/{catId}")
    public ResponseEntity<Void> deleteCatById(@PathVariable Long catId) {
        catService.deleteCatById(catId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
