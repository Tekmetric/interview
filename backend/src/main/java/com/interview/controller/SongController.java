package com.interview.controller;

import com.interview.dto.SongDto;
import com.interview.dto.SongListDto;
import com.interview.service.SongService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "Songs", description = "Operations for managing songs")
@RestController
@RequestMapping("/api/songs")
public class SongController {

    private final SongService songService;

    @Autowired
    public SongController(SongService songService) {
        this.songService = songService;
    }

    @ApiOperation(value = "Get all songs", notes = "Returns a paginated list of all songs with artist and album information")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of songs")
    })
    @GetMapping
    public ResponseEntity<Page<SongListDto>> getAllSongs(
            @ApiParam(value = "Pagination parameters (page, size, sort)")
            @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<SongListDto> songs = songService.getAllSongs(pageable);
        return ResponseEntity.ok(songs);
    }

    @ApiOperation(value = "Get song by ID", notes = "Returns a specific song with its artist and album associations")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved song"),
            @ApiResponse(code = 404, message = "Song not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SongDto> getSong(
            @ApiParam(value = "Song ID", required = true) @PathVariable Long id) {
        SongDto song = songService.getSong(id);
        return ResponseEntity.ok(song);
    }

    @ApiOperation(value = "Create new song", notes = "Creates a new song associated with an artist and optionally with albums. Sends real-time notifications.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Song successfully created"),
            @ApiResponse(code = 400, message = "Invalid input - missing required fields or invalid data"),
            @ApiResponse(code = 404, message = "Artist or album not found")
    })
    @PostMapping
    public ResponseEntity<SongDto> createSong(
            @ApiParam(value = "Song data (requires artistId, title, length, releaseDate)", required = true)
            @Valid @RequestBody SongDto songDto) {
        SongDto createdSong = songService.createSong(songDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSong);
    }

    @ApiOperation(value = "Update song", notes = "Updates an existing song including artist and album associations. Sends real-time notifications.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Song successfully updated"),
            @ApiResponse(code = 404, message = "Song, artist, or album not found"),
            @ApiResponse(code = 400, message = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SongDto> updateSong(
            @ApiParam(value = "Song ID", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated song data", required = true) @Valid @RequestBody SongDto songDto) {
        SongDto updatedSong = songService.updateSong(id, songDto);
        return ResponseEntity.ok(updatedSong);
    }

    @ApiOperation(value = "Delete song", notes = "Deletes a song. Albums associated with the song are NOT deleted. Sends real-time notifications.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Song successfully deleted"),
            @ApiResponse(code = 404, message = "Song not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(
            @ApiParam(value = "Song ID", required = true) @PathVariable Long id) {
        songService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }
}
