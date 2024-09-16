package com.interview.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.interview.domain.enumeration.AlbumPhotoType;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "album_photo")
public class AlbumPhoto extends AbstractAuditingEntity{

    @Id
    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "rank")
    @Enumerated(EnumType.STRING)
    private AlbumPhotoType rank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")
    @JsonIgnore
    private VinylRecord vinylRecord;

    public AlbumPhoto() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public VinylRecord getVinylRecord() {
        return vinylRecord;
    }

    public void setVinylRecord(VinylRecord vinylRecord) {
        this.vinylRecord = vinylRecord;
    }

    public AlbumPhotoType getRank() {
        return rank;
    }

    public void setRank(AlbumPhotoType rank) {
        this.rank = rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlbumPhoto that = (AlbumPhoto) o;
        return Objects.equals(uuid, that.uuid) && Objects.equals(imageUrl, that.imageUrl) && rank == that.rank && Objects.equals(vinylRecord, that.vinylRecord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, imageUrl, rank, vinylRecord);
    }
}
