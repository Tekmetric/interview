package com.interview.resource;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.interview.dto.DataPayload;
import com.interview.entity.DataRecord;
import com.interview.entity.DataRepository;
import com.interview.util.DataMapper;

@RestController
public class CrudDataResource {

    @Autowired
    private DataRepository repository;

    @Autowired
    private DataMapper mapper;


    @GetMapping("/api/data/list")
    public List<DataPayload> dataGetMany() {
        List<DataRecord> records = repository.findAll();
        // System.out.println("Found: " + records);
        return records.stream()
                .map(mapper::dataRecordToPayload)
                .toList();
    }

    @GetMapping("/api/data/{id}")
    public DataPayload dataGetOne(@PathVariable("id") UUID id) {
        // System.out.println("getting ID: " + id);
        return repository.findOneById(id)
                .map(mapper::dataRecordToPayload)
                .orElseThrow(() -> new EntityNotFoundException("no record found for ID: " + id));
    }

    @PostMapping("/api/data")
    public DataPayload dataCreateOne(@RequestBody DataPayload payload) {
        // Spring + H2 doesn't respect fields generated in the DB,
        // although the schema is written to populate them there;
        // fill them here as a workaround
        Instant now = Instant.now();
        payload.setCreated(now);
        payload.setUpdated(now);
        payload.setId(UUID.randomUUID());
        DataRecord input = mapper.dataPayloadToRecord(payload);
        DataRecord saved = repository.save(input);
        return mapper.dataRecordToPayload(saved);
    }

    @PutMapping("/api/data/{id}")
    public DataPayload dataUpdateOne(@PathVariable("id") UUID id, @RequestBody DataPayload payload) {
        if (payload.getId() == null) payload.setId(id);
        assert id.equals(payload.getId()) : "path ID must match payload";
        // H2 doesn't handle partial updates gracefully without more work,
        // so pull the existing record out and then merge relevant fields
        DataRecord input = repository.findOneById(id)
                .orElseThrow(() -> new EntityNotFoundException("no record found for ID: " + id));
        if (payload.getName() != null) input.setName(payload.getName());
        if (payload.getCount() != null) input.setCount(payload.getCount());
        input.setUpdated(Instant.now());
        DataRecord saved = repository.save(input);
        return mapper.dataRecordToPayload(saved);
    }

    @DeleteMapping("/api/data/{id}")
    public void dataDeleteOne(@PathVariable("id") UUID id) {
        repository.deleteById(id);
    }

}
