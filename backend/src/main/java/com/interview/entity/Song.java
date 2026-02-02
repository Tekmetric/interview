package com.interview.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "song")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotNull
    @Column(nullable = false)
    private Long lengthInSeconds;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @ManyToMany(mappedBy = "songs")
    private List<Album> albums = new ArrayList<>();

    public Song() {
    }

    public Song(String title, Long lengthInSeconds, LocalDate releaseDate, Artist artist) {
        this.title = title;
        this.lengthInSeconds = lengthInSeconds;
        this.releaseDate = releaseDate;
        this.artist = artist;
        if (artist != null) {
            artist.addSongInternal(this);
        }
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

    public Duration getLength() {
        return lengthInSeconds != null ? Duration.ofSeconds(lengthInSeconds) : null;
    }

    public void setLength(Duration length) {
        this.lengthInSeconds = length != null ? length.getSeconds() : null;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        if (this.artist != null) {
            this.artist.removeSongInternal(this);
        }
        this.artist = artist;
        if (artist != null) {
            artist.addSongInternal(this);
        }
    }

    void setArtistInternal(Artist artist) {
        this.artist = artist;
    }

    public List<Album> getAlbums() {
        return Collections.unmodifiableList(albums == null ? new ArrayList<>() : albums);
    }

    public void setAlbums(List<Album> albums) {
        // Remove from all current albums (manages both sides via public API)
        List<Album> currentAlbums = new ArrayList<>(this.albums);
        for (Album album : currentAlbums) {
            album.removeSongInternal(this);
        }

        this.albums.clear();

        // Add to new albums (manages both sides via public API)
        if (albums != null) {
            for (Album album : albums) {
                album.addSongInternal(this);
            }
            this.albums.addAll(albums);
        }
    }

    void addAlbumInternal(Album album) {
        if (!this.albums.contains(album)) {
            this.albums.add(album);
        }
    }

    void removeAlbumInternal(Album album) {
        if  (this.albums.contains(album)) {
            this.albums.remove(album);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(title, song.title) && Objects.equals(lengthInSeconds, song.lengthInSeconds)
                && Objects.equals(releaseDate, song.releaseDate) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, lengthInSeconds, releaseDate);
    }
}
