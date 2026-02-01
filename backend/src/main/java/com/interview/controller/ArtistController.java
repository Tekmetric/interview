package com.interview.controller;

import com.interview.dto.AlbumListDto;
import com.interview.dto.ArtistDto;
import com.interview.dto.ArtistListDto;
import com.interview.dto.SongListDto;
import com.interview.service.AlbumService;
import com.interview.service.ArtistService;
import com.interview.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @GetMapping
    public ResponseEntity<Page<ArtistListDto>> getAllArtists(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ArtistListDto> artists = artistService.getAllArtists(pageable);
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistDto> getArtist(@PathVariable Long id) {
        ArtistDto artist = artistService.getArtist(id);
        return ResponseEntity.ok(artist);
    }

    @PostMapping
    public ResponseEntity<ArtistDto> createArtist(@Valid @RequestBody ArtistDto artistDto) {
        ArtistDto createdArtist = artistService.createArtist(artistDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArtist);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistDto> updateArtist(@PathVariable Long id, @Valid @RequestBody ArtistDto artistDto) {
        ArtistDto updatedArtist = artistService.updateArtist(id, artistDto);
        return ResponseEntity.ok(updatedArtist);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Long id) {
        artistService.deleteArtist(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/songs")
    public ResponseEntity<Page<SongListDto>> getArtistSongs(
            @PathVariable Long id,
            @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<SongListDto> songs = songService.getSongsByArtist(id, pageable);
        return ResponseEntity.ok(songs);
    }

    @GetMapping("/{id}/albums")
    public ResponseEntity<Page<AlbumListDto>> getArtistAlbums(
            @PathVariable Long id,
            @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<AlbumListDto> albums = albumService.getAlbumsByArtist(id, pageable);
        return ResponseEntity.ok(albums);
    }
}
