package com.interview.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "album")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "song_album",
        joinColumns = @JoinColumn(name = "album_id"),
        inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    private List<Song> songs = new ArrayList<>();

    public Album() {
    }

    public Album(String title, LocalDate releaseDate, Artist artist) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.artist = artist;
        if (artist != null) {
            artist.addAlbumInternal(this);
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
            this.artist.removeAlbumInternal(this);
        }
        this.artist = artist;
        if (artist != null) {
            artist.addAlbumInternal(this);
        }
    }

    void setArtistInternal(Artist artist) {
        this.artist = artist;
    }
    public List<Song> getSongs() {
        return Collections.unmodifiableList(songs);
    }

    public void setSongs(List<Song> songs) {
        // Remove all current songs (manages both sides via public API)
        List<Song> currentSongs = new ArrayList<>(this.songs);
        for (Song song : currentSongs) {
            this.removeSong(song);
        }

        // Add all new songs (manages both sides via public API)
        if (songs != null) {
            for (Song song : songs) {
                this.addSong(song);
            }
        }
    }

    public void addSong(Song song) {
        addSongInternal(song);
        song.addAlbumInternal(this);
    }

    void addSongInternal(Song song) {
        if (!this.songs.contains(song)) {
            this.songs.add(song);
        }
    }

    public void removeSong(Song song) {
        removeSongInternal(song);
        // Use internal accessor to manage bidirectional relationship
        song.removeAlbumInternal(this);
    }

    void removeSongInternal(Song song) {
        this.songs.remove(song);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return Objects.equals(title, album.title)
                && Objects.equals(releaseDate, album.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, releaseDate);
    }
}
