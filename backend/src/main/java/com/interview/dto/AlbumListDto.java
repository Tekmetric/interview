package com.interview.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AlbumListDto {

    private Long id;
    private String title;
    private LocalDate releaseDate;
    private ArtistRefDto artist;
    private Integer songCount;
    private List<SongRefDto> songs = new ArrayList<>();

    public AlbumListDto() {
    }

    public AlbumListDto(Long id, String title, LocalDate releaseDate, ArtistRefDto artist,
                        Integer songCount, List<SongRefDto> songs) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.artist = artist;
        this.songCount = songCount;
        this.songs = songs != null ? songs : new ArrayList<>();
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

    public Integer getSongCount() {
        return songCount;
    }

    public void setSongCount(Integer songCount) {
        this.songCount = songCount;
    }

    public List<SongRefDto> getSongs() {
        return songs;
    }

    public void setSongs(List<SongRefDto> songs) {
        this.songs = songs;
    }
}
