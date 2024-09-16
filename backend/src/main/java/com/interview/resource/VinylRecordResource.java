package com.interview.resource;

import com.interview.service.VinylRecordService;
import com.interview.service.dto.VinylRecordDTO;
import com.interview.service.dto.VinylRecordPayloadDTO;
import com.interview.service.dto.VinylRecordPresentationDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.interview.resource.exception.ErrorConstants.ERR_INVALID_RECORD_ID;

@RestController
@RequestMapping("/api")
public class VinylRecordResource {

    private final VinylRecordService vinylRecordService;

    public VinylRecordResource(VinylRecordService vinylRecordService) {
        this.vinylRecordService = vinylRecordService;
    }

    @Operation(summary = "Find a vinyl record by id",
            description = "Finds a comprehensive projection of the vinyl record entity (This could be used on the <Vinyl Details> page>)")
    @GetMapping("/vinyl/{id}")
    public ResponseEntity<VinylRecordDTO> getRecord(@PathVariable("id") Long id) {
        return ResponseEntity.ok(vinylRecordService.getVinylRecordById(id));
    }

    @Operation(summary = "Find either all the vinyl records, or some of them filtered by a search query that filters based on Title or Artist",
            description = "using the industry-classic Pageable to control how many entries are fetched, and also how sorting works")
    @GetMapping("/vinyl")
    public ResponseEntity<List<VinylRecordPresentationDTO>> getAllRecords(@RequestParam(name = "searchQuery", required = false)
                                                                          @Parameter(description = "Search query by title or artist of record") String searchQuery,
                                                                          @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(vinylRecordService.findAll(searchQuery, pageable));
    }

    @Operation(summary = "Add a new Title/Artists combination",
            description = "Creates a new VinylRecord entity with only the necessary information. This was a longer thought process that also involves a frontend app that would create the entity with minumum information, redirect to the <VinylDetails> page where we already have a valid id that could be used in uploading the album cover and back pictures." +
                    " Artists are considered to be chosen from a dropdown menu which lets the user select either an existing artist, or opens up a modal menu that lets them add a new one.")
    @PostMapping("/vinyl")
    public ResponseEntity<Long> createNewRecord(@Valid @RequestBody VinylRecordPayloadDTO vinylRecordDTO) {
        return ResponseEntity.ok(vinylRecordService.createNewVinylRecord(vinylRecordDTO));
    }

    @Operation(summary = "Update the Vinyl Record",
            description = "Updates the fields in the Vinyl Record entity")
    @PutMapping("/vinyl/{id}")
    public ResponseEntity<VinylRecordDTO> updateRecord(@NotNull(message = ERR_INVALID_RECORD_ID) @PathVariable("id") Long id,
                                                       @RequestBody VinylRecordDTO vinylRecordDTO) {
        return ResponseEntity.ok(vinylRecordService.updateVinylRecord(id, vinylRecordDTO));
    }

    @Operation(summary = "Delete the Vinyl Record",
            description = "Deletes the entity specified by the id, if exists, together with the association to the artists, and the album photos uploaded.")
    @DeleteMapping("/vinyl/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable("id") Long id) {
        vinylRecordService.deleteVinylRecordById(id);
        return ResponseEntity.ok().build();
    }
}
