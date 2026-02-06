package com.interview.dto;

public class ArtistListDto {

    private Long id;
    private String name;
    private Integer songCount;
    private Integer albumCount;

    public ArtistListDto() {
    }

    public ArtistListDto(Long id, String name, Integer songCount, Integer albumCount) {
        this.id = id;
        this.name = name;
        this.songCount = songCount;
        this.albumCount = albumCount;
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

    public Integer getSongCount() {
        return songCount;
    }

    public void setSongCount(Integer songCount) {
        this.songCount = songCount;
    }

    public Integer getAlbumCount() {
        return albumCount;
    }

    public void setAlbumCount(Integer albumCount) {
        this.albumCount = albumCount;
    }
}
