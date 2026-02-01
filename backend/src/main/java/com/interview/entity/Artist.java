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
        this.songs = songs;
    }

    /**
     * Adds a song to this artist and manages the bidirectional relationship.
     * Note: This is the inverse side; the relationship is owned by Song.
     */
    public void addSong(Song song) {
        if (!this.songs.contains(song)) {
            this.songs.add(song);
            if (song.getArtist() != this) {
                song.setArtist(this);
            }
        }
    }

    /**
     * Removes a song from this artist and manages the bidirectional relationship.
     */
    public void removeSong(Song song) {
        this.songs.remove(song);
        if (song.getArtist() == this) {
            song.setArtist(null);
        }
    }

    public List<Album> getAlbums() {
        return Collections.unmodifiableList(albums);
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    /**
     * Adds an album to this artist and manages the bidirectional relationship.
     * Note: This is the inverse side; the relationship is owned by Album.
     */
    public void addAlbum(Album album) {
        if (!this.albums.contains(album)) {
            this.albums.add(album);
            if (album.getArtist() != this) {
                album.setArtist(this);
            }
        }
    }

    /**
     * Removes an album from this artist and manages the bidirectional relationship.
     */
    public void removeAlbum(Album album) {
        this.albums.remove(album);
        if (album.getArtist() == this) {
            album.setArtist(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        return Objects.equals(id, artist.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
