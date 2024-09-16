package com.interview.service.dto;

import com.interview.domain.AlbumPhoto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class VinylRecordDTO {

    private Long id;
    private String title;
    private String albumType;
    private String eanCode;
    private String genre;
    private Set<ArtistDTO> artists;
    private List<AlbumPhoto> albumPhotos;
    private LocalDate acquisitionDate;
    private String yearOfRelease;
    private String label;
    private Integer numberOfDiscs;

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

    public String getAlbumType() {
        return albumType;
    }

    public void setAlbumType(String albumType) {
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

    public Set<ArtistDTO> getArtists() {
        return artists;
    }

    public void setArtists(Set<ArtistDTO> artists) {
        this.artists = artists;
    }

    public List<AlbumPhoto> getAlbumPhotos() {
        return albumPhotos;
    }

    public void setAlbumPhotos(List<AlbumPhoto> albumPhotos) {
        this.albumPhotos = albumPhotos;
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
}
