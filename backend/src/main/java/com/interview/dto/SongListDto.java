package com.interview.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SongListDto {

    private Long id;
    private String title;
    private Long lengthInSeconds;
    private LocalDate releaseDate;
    private ArtistRefDto artist;
    private List<AlbumRefDto> albums = new ArrayList<>();

    public SongListDto() {
    }

    public SongListDto(Long id, String title, Long lengthInSeconds, LocalDate releaseDate,
                       ArtistRefDto artist, List<AlbumRefDto> albums) {
        this.id = id;
        this.title = title;
        this.lengthInSeconds = lengthInSeconds;
        this.releaseDate = releaseDate;
        this.artist = artist;
        this.albums = albums != null ? albums : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getLengthInSeconds() {
        return lengthInSeconds;
    }

    public void setLengthInSeconds(Long lengthInSeconds) {
        this.lengthInSeconds = lengthInSeconds;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public ArtistRefDto getArtist() {
        return artist;
    }

    public void setArtist(ArtistRefDto artist) {
        this.artist = artist;
    }

    public List<AlbumRefDto> getAlbums() {
        return albums;
    }

    public void setAlbums(List<AlbumRefDto> albums) {
        this.albums = albums;
    }
}
