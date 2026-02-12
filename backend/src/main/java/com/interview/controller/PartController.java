package com.interview.controller;

import com.interview.exception.ResourceNotFoundException;
import com.interview.model.Part;
import com.interview.service.PartService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class PartController {
    private final PartService partService;

    @GetMapping("/api/part/{partId}")
    public Part getPart(@PathVariable Long partId) {
        return partService.get(partId)
                .orElseThrow(() -> new ResourceNotFoundException("No part found with id: " + partId));
    }

    @GetMapping("/api/parts")
    public Page<Part> getAllParts(Pageable pageable) {
        return partService.getAll(pageable);
    }

    @PostMapping("/api/part")
    public Part addPart(@RequestBody Part part) {
        return partService.create(part);
    }

    @PutMapping("/api/part/{partId}")
    public Part updatePart(@PathVariable Long partId, @RequestBody Part part) {
        return partService.update(part.setId(partId));
    }

    @PostMapping("/api/part/{partId}/inventory")
    public Part updateInventory(@PathVariable Long partId, @RequestParam int inventoryDelta) {
        return partService.adjustInventory(partId, inventoryDelta);
    }

    @DeleteMapping("/api/part/{partId}")
    public void deletePart(@PathVariable Long partId) {
        partService.delete(partId);
    }
}
