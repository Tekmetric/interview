package com.interview.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SongDto {

    private Long id;

    @NotBlank(message = "Song title is required")
    private String title;

    @NotNull(message = "Song length is required")
    private Long lengthInSeconds;

    private LocalDate releaseDate;

    @NotNull(message = "Artist ID is required")
    private Long artistId;

    private List<Long> albumIds = new ArrayList<>();

    public SongDto() {
    }

    public SongDto(Long id, String title, Long lengthInSeconds, LocalDate releaseDate, Long artistId, List<Long> albumIds) {
        this.id = id;
        this.title = title;
        this.lengthInSeconds = lengthInSeconds;
        this.releaseDate = releaseDate;
        this.artistId = artistId;
        this.albumIds = albumIds != null ? albumIds : new ArrayList<>();
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

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public List<Long> getAlbumIds() {
        return albumIds;
    }

    public void setAlbumIds(List<Long> albumIds) {
        this.albumIds = albumIds;
    }
}
