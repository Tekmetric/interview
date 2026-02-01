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
        this.artist = artist;
    }

    public List<Album> getAlbums() {
        return Collections.unmodifiableList(albums);
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    /**
     * Package-private method for internal use by Album to manage bidirectional relationship.
     * Should not be called by external code.
     */
    List<Album> getAlbumsInternal() {
        return albums;
    }

    public void removeFromAllAlbums() {
        // Create a copy to avoid ConcurrentModificationException
        List<Album> albumsCopy = new ArrayList<>(this.albums);
        for (Album album : albumsCopy) {
            album.removeSong(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(id, song.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
