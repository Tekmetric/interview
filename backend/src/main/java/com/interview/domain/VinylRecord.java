package com.interview.domain;

import com.interview.domain.enumeration.AlbumType;
import com.interview.service.dto.VinylRecordDTO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "vinyl_record")
public class VinylRecord extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    @NotBlank(message = "Title should not be blank")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "album_type")
    private AlbumType albumType;

    @Column(name = "ean_code")
    private String eanCode;

    @Column(name = "genre")
    private String genre;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "vinyl_record_artist",
            joinColumns = @JoinColumn(name = "record_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "artists_id", referencedColumnName = "id"))
    private Set<Artist> artists = new HashSet<>();

    @OneToMany(mappedBy = "vinylRecord", cascade = CascadeType.ALL)
    private List<AlbumPhoto> albumPhotos = new ArrayList<>();

    @Column(name = "acquisition_date")
    private LocalDate acquisitionDate;

    @Column(name = "release_year")
    private String yearOfRelease;

    @Column(name = "label")
    private String label;

    @Column(name = "number_of_discs")
    private Integer numberOfDiscs;

    public VinylRecord() {
    }

    public VinylRecord(String title, Set<Artist> artists) {
        this.title = title;
        this.artists = artists;
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

    public AlbumType getAlbumType() {
        return albumType;
    }

    public void setAlbumType(AlbumType albumType) {
        this.albumType = albumType;
    }

    public String getEanCode() {
        return eanCode;
    }

    public void setEanCode(String eanCode) {
        this.eanCode = eanCode;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Set<Artist> getArtists() {
        return artists;
    }

    public void setArtists(Set<Artist> artists) {
        this.artists = artists;
    }

    public List<AlbumPhoto> getAlbumPhotos() {
        return albumPhotos;
    }

    public void setAlbumPhotos(List<AlbumPhoto> albumPhotoUrls) {
        this.albumPhotos = albumPhotoUrls;
    }

    public LocalDate getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(LocalDate acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public String getYearOfRelease() {
        return yearOfRelease;
    }

    public void setYearOfRelease(String yearOfRelease) {
        this.yearOfRelease = yearOfRelease;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getNumberOfDiscs() {
        return numberOfDiscs;
    }

    public void setNumberOfDiscs(Integer numberOfDiscs) {
        this.numberOfDiscs = numberOfDiscs;
    }

    public void setFieldsFromDTO(VinylRecordDTO dto, Set<Artist> artists){
        this.setTitle(dto.getTitle());
        this.setArtists(artists);
        this.setAlbumPhotos(dto.getAlbumPhotos());
        this.setGenre(dto.getGenre());
        this.setEanCode(dto.getEanCode());
        this.setYearOfRelease(dto.getYearOfRelease());
        this.setNumberOfDiscs(dto.getNumberOfDiscs());
        this.setAcquisitionDate(dto.getAcquisitionDate());
        this.setLabel(dto.getLabel());
        this.setAlbumType(AlbumType.valueOf(dto.getAlbumType()));
        this.setLastModifiedBy("my_lovely_app_user@gmail.com");
        this.setLastModifiedDate(LocalDateTime.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VinylRecord that = (VinylRecord) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && albumType == that.albumType && Objects.equals(eanCode, that.eanCode) && Objects.equals(genre, that.genre) && Objects.equals(artists, that.artists) && Objects.equals(albumPhotos, that.albumPhotos) && Objects.equals(acquisitionDate, that.acquisitionDate) && Objects.equals(yearOfRelease, that.yearOfRelease) && Objects.equals(label, that.label) && Objects.equals(numberOfDiscs, that.numberOfDiscs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, albumType, eanCode, genre, artists, albumPhotos, acquisitionDate, yearOfRelease, label, numberOfDiscs);
    }
}
