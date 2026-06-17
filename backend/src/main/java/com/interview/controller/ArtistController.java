package com.interview.controller;

import com.interview.dto.AlbumListDto;
import com.interview.dto.ArtistDto;
import com.interview.dto.ArtistListDto;
import com.interview.dto.SongListDto;
import com.interview.service.AlbumService;
import com.interview.service.ArtistService;
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

@Api(tags = "Artists", description = "Operations for managing artists")
@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    private final ArtistService artistService;
    private final SongService songService;
    private final AlbumService albumService;

    @Autowired
    public ArtistController(ArtistService artistService, SongService songService, AlbumService albumService) {
        this.artistService = artistService;
        this.songService = songService;
        this.albumService = albumService;
    }

    @ApiOperation(value = "Get all artists", notes = "Returns a paginated list of all artists with song and album counts")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of artists")
    })
    @GetMapping
    public ResponseEntity<Page<ArtistListDto>> getAllArtists(
            @ApiParam(value = "Pagination parameters (page, size, sort)")
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ArtistListDto> artists = artistService.getAllArtists(pageable);
        return ResponseEntity.ok(artists);
    }

    @ApiOperation(value = "Get artist by ID", notes = "Returns a specific artist by their ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved artist"),
            @ApiResponse(code = 404, message = "Artist not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ArtistDto> getArtist(
            @ApiParam(value = "Artist ID", required = true) @PathVariable Long id) {
        ArtistDto artist = artistService.getArtist(id);
        return ResponseEntity.ok(artist);
    }

    @ApiOperation(value = "Create new artist", notes = "Creates a new artist and sends real-time notifications")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Artist successfully created"),
            @ApiResponse(code = 400, message = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<ArtistDto> createArtist(
            @ApiParam(value = "Artist data", required = true) @Valid @RequestBody ArtistDto artistDto) {
        ArtistDto createdArtist = artistService.createArtist(artistDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArtist);
    }

    @ApiOperation(value = "Update artist", notes = "Updates an existing artist and sends real-time notifications")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Artist successfully updated"),
            @ApiResponse(code = 404, message = "Artist not found"),
            @ApiResponse(code = 400, message = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ArtistDto> updateArtist(
            @ApiParam(value = "Artist ID", required = true) @PathVariable Long id,
            @ApiParam(value = "Updated artist data", required = true) @Valid @RequestBody ArtistDto artistDto) {
        ArtistDto updatedArtist = artistService.updateArtist(id, artistDto);
        return ResponseEntity.ok(updatedArtist);
    }

    @ApiOperation(value = "Delete artist", notes = "Deletes an artist and cascades to all associated songs and albums. Sends real-time notifications.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Artist successfully deleted"),
            @ApiResponse(code = 404, message = "Artist not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(
            @ApiParam(value = "Artist ID", required = true) @PathVariable Long id) {
        artistService.deleteArtist(id);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Get artist's songs", notes = "Returns a paginated list of all songs by the specified artist")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved songs"),
            @ApiResponse(code = 404, message = "Artist not found")
    })
    @GetMapping("/{id}/songs")
    public ResponseEntity<Page<SongListDto>> getArtistSongs(
            @ApiParam(value = "Artist ID", required = true) @PathVariable Long id,
            @ApiParam(value = "Pagination parameters (page, size, sort)")
            @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<SongListDto> songs = songService.getSongsByArtist(id, pageable);
        return ResponseEntity.ok(songs);
    }

    @ApiOperation(value = "Get artist's albums", notes = "Returns a paginated list of all albums by the specified artist")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved albums"),
            @ApiResponse(code = 404, message = "Artist not found")
    })
    @GetMapping("/{id}/albums")
    public ResponseEntity<Page<AlbumListDto>> getArtistAlbums(
            @ApiParam(value = "Artist ID", required = true) @PathVariable Long id,
            @ApiParam(value = "Pagination parameters (page, size, sort)")
            @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<AlbumListDto> albums = albumService.getAlbumsByArtist(id, pageable);
        return ResponseEntity.ok(albums);
    }
}
