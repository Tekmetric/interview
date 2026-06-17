package com.interview.controller;

import com.interview.dto.AlbumDto;
import com.interview.dto.AlbumListDto;
import com.interview.dto.SongListDto;
import com.interview.service.AlbumService;
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

@Api(tags = "Albums", description = "Operations for managing albums")
@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumService albumService;
    private final SongService songService;

    @Autowired
    public AlbumController(AlbumService albumService, SongService songService) {
        this.albumService = albumService;
        this.songService = songService;
    }

    @ApiOperation(value = "Get all albums", notes = "Returns a paginated list of all albums with artist and song information")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of albums")
    })
    @GetMapping
    public ResponseEntity<Page<AlbumListDto>> getAllAlbums(
            @ApiParam(value = "Pagination parameters (page, size, sort)")
            @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<AlbumListDto> albums = albumService.getAllAlbums(pageable);
        return ResponseEntity.ok(albums);
    }

    @ApiOperation(value = "Get album by ID", notes = "Returns a specific album with its artist and song associations")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved album"),
            @ApiResponse(code = 404, message = "Album not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlbumDto> getAlbum(
            @ApiParam(value = "Album ID", required = true) @PathVariable Long id) {
        AlbumDto album = albumService.getAlbum(id);
        return ResponseEntity.ok(album);
    }

    @ApiOperation(value = "Create new album", notes = "Creates a new album associated with an artist and optionally with songs. Sends real-time notifications.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Album successfully created"),
            @ApiResponse(code = 400, message = "Invalid input - missing required fields or invalid data"),
            @ApiResponse(code = 404, message = "Artist or song not found")
    })
    @PostMapping
    public ResponseEntity<AlbumDto> createAlbum(
            @ApiParam(value = "Album data (requires artistId, title, releaseDate)", required = true)
            @Valid @RequestBody AlbumDto albumDto) {
        AlbumDto createdAlbum = albumService.createAlbum(albumDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAlbum);
    }

    @ApiOperation(value = "Update album", notes = "Updates an existing album including artist and song associations. Sends real-time notifications.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Album successfully updated"),
            @ApiResponse(code = 404, message = "Album, artist, or song not found"),
            @ApiResponse(code = 400, message = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AlbumDto> updateAlbum(
            @ApiParam(value = "Album ID", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated album data", required = true) @Valid @RequestBody AlbumDto albumDto) {
        AlbumDto updatedAlbum = albumService.updateAlbum(id, albumDto);
        return ResponseEntity.ok(updatedAlbum);
    }

    @ApiOperation(value = "Delete album", notes = "Deletes an album. Songs associated with the album are NOT deleted. Sends real-time notifications.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Album successfully deleted"),
            @ApiResponse(code = 404, message = "Album not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(
            @ApiParam(value = "Album ID", required = true) @PathVariable Long id) {
        albumService.deleteAlbum(id);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Get album's songs", notes = "Returns a paginated list of all songs on the specified album")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved songs"),
            @ApiResponse(code = 404, message = "Album not found")
    })
    @GetMapping("/{id}/songs")
    public ResponseEntity<Page<SongListDto>> getAlbumSongs(
            @ApiParam(value = "Album ID", required = true) @PathVariable Long id,
            @ApiParam(value = "Pagination parameters (page, size, sort)")
            @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<SongListDto> songs = songService.getSongsByAlbum(id, pageable);
        return ResponseEntity.ok(songs);
    }
}
