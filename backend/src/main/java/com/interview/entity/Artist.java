package com.interview.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "artist")
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Song> songs = new ArrayList<>();

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Album> albums = new ArrayList<>();

    public Artist() {
    }

    public Artist(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    /**
     * Adds a song to this artist and manages the bidirectional relationship.
     * Note: This is the inverse side; the relationship is owned by Song.
     */
    public void addSong(Song song) {
        addSongInternal(song);
        song.setArtist(this);
    }

    void addSongInternal(Song song) {
        if (!this.songs.contains(song)) {
            this.songs.add(song);
        }
    }

    /**
     * Removes a song from this artist and manages the bidirectional relationship.
     */
    public void removeSong(Song song) {
        this.songs.remove(song);
        if (song.getArtist() == this) {
            song.setArtistInternal(null);
        }
    }

    void removeSongInternal(Song song) {
        this.songs.remove(song);
    }

    public List<Album> getAlbums() {
        return Collections.unmodifiableList(albums);
    }

    public void setAlbums(List<Album> albums) {
        // Remove all current albums (manages both sides via public API)
        List<Album> currentAlbums = new ArrayList<>(this.albums);
        for (Album album : currentAlbums) {
            album.setArtistInternal(null);
        }

        this.albums.clear();

        // Add all new albums (manages both sides via public API)
        if (albums != null) {
            for (Album album : albums) {
                album.setArtistInternal(this);
            }
            this.albums.addAll(albums);
        }
    }

    /**
     * Adds an album to this artist and manages the bidirectional relationship.
     * Note: This is the inverse side; the relationship is owned by Album.
     */
    public void addAlbum(Album album) {
        if (!this.albums.contains(album)) {
            this.albums.add(album);
            if (album.getArtist() != this) {
                album.setArtistInternal(this);
            }
        }
    }

    void addAlbumInternal(Album album) {
        if (!this.albums.contains(album)) {
            this.albums.add(album);
        }
    }

    /**
     * Removes an album from this artist and manages the bidirectional relationship.
     */
    public void removeAlbum(Album album) {
        this.albums.remove(album);
        if (album.getArtist() == this) {
            album.setArtistInternal(null);
        }
    }

    void removeAlbumInternal(Album album) {
        this.albums.remove(album);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        return Objects.equals(name, artist.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
