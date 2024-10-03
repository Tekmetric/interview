package com.interview.resource;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.interview.model.Popstar;
import com.interview.repositories.PopstarRepository;


@RestController
public class PopstarResource {

    private PopstarRepository popstarRepository;

    public PopstarResource(PopstarRepository popstarRepository) {
        this.popstarRepository = popstarRepository;
    }

    @PostMapping("/api/popstars/create")
    public Popstar create(@RequestBody Popstar popstar) {
        if (this.popstarRepository.findById(popstar.getId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Popstar with id %s already exits", popstar.getId()));
        }
        return this.popstarRepository.save(popstar);
    }

    @PutMapping("/api/popstars/update")
    public Popstar update(@RequestBody Popstar popstar) {
        if (this.popstarRepository.findById(popstar.getId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return this.popstarRepository.save(popstar);
    }

    @GetMapping("/api/popstars/all")
    public List<Popstar> getAllPopstars() {
        return this.popstarRepository.findAll();
    }

    @GetMapping("/api/popstars/{id}")
    public Popstar getPopstar(@PathVariable Long id) {
        return this.popstarRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/api/popstars/delete/{id}")
    public void deletePopstar(@PathVariable Long id) {
        this.popstarRepository.deleteById(id);
    }
}
