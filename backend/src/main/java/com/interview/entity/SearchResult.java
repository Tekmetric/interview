package com.interview.entity;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Immutable
@Subselect(
    "SELECT " +
    "    id, " +
    "    'ARTIST' as entity_type, " +
    "    name as name, " +
    "    name as artist_name, " +
    "    NULL as release_date " +
    "FROM artist " +
    "UNION ALL " +
    "SELECT " +
    "    s.id, " +
    "    'SONG' as entity_type, " +
    "    s.title as name, " +
    "    a.name as artist_name, " +
    "    s.release_date as release_date " +
    "FROM song s " +
    "JOIN artist a ON s.artist_id = a.id " +
    "UNION ALL " +
    "SELECT " +
    "    al.id, " +
    "    'ALBUM' as entity_type, " +
    "    al.title as name, " +
    "    a.name as artist_name, " +
    "    al.release_date as release_date " +
    "FROM album al " +
    "JOIN artist a ON al.artist_id = a.id"
)
public class SearchResult {

    @Id
    private Long id;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "name")
    private String name;

    @Column(name = "artist_name")
    private String artistName;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    public SearchResult() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchResult that = (SearchResult) o;
        return Objects.equals(id, that.id) && Objects.equals(entityType, that.entityType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, entityType);
    }
}
