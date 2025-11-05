package com.interview.service;

import com.interview.exception.BadRequestException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.model.Part;
import com.interview.repository.PartRepository;
import com.interview.repository.WorkOrderPartRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PartService {
    private final PartRepository partRepository;
    private final WorkOrderPartRepository workOrderPartRepository;

    public Optional<Part> get(Long partId) {
        return partRepository.findById(partId).map(Part::fromEntity);
    }

    public Page<Part> getAll(Pageable pageable) {
        return partRepository.findAll(pageable)
                .map(Part::fromEntity);
    }

    @Transactional
    public Part adjustInventory(Long partId, int delta) {
        if (!partRepository.existsById(partId)) {
            throw new ResourceNotFoundException("Part not found: " + partId);
        }

        if (delta == 0) {
            return get(partId)
                    .orElseThrow(() -> new ResourceNotFoundException("Part not found: " + partId));
        }

        // adjustInventory returns the # of updated rows, 0 indicates that no update was made
        int updated = partRepository.adjustInventory(partId, delta);
        if (updated == 0) {
            throw new BadRequestException("Inventory update rejected for part " + partId);
        }
        return get(partId)
                .orElseThrow(() -> new ResourceNotFoundException("Part not found after update: " + partId));
    }

    @Transactional
    public Part create(Part part) {
        var entity = part.toEntity();
        entity.setId(null); // ID will be set by DB
        var created = partRepository.save(entity);
        return Part.fromEntity(created);
    }

    @Transactional
    public Part update(Part part) {
        if (part.getId() == null) {
            throw new BadRequestException("Part ID is required for update");
        }
        var existing = partRepository.findById(part.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Part not found: " + part.getId()));
        existing.setName(part.getName());

        var updated = partRepository.save(existing);
        return Part.fromEntity(updated);
    }

    @Transactional
    public void delete(Long partId) {
        if (!workOrderPartRepository.findWorkOrdersByPartId(partId).isEmpty()) {
            throw new BadRequestException("Unable to delete Part with id " + partId + ". Part is still in use.");
        }
        partRepository.deleteById(partId);
    }
}
