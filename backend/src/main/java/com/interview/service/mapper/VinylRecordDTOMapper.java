package com.interview.service.mapper;

import com.interview.domain.AlbumPhoto;
import com.interview.domain.Artist;
import com.interview.domain.VinylRecord;
import com.interview.domain.enumeration.AlbumPhotoType;
import com.interview.domain.enumeration.AlbumType;
import com.interview.service.dto.VinylRecordDTO;
import com.interview.service.dto.VinylRecordPresentationDTO;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class VinylRecordDTOMapper implements GenericMapper<VinylRecord, VinylRecordDTO, VinylRecordPresentationDTO> {

    private final ArtistDTOMapper artistDtoMapper;

    public VinylRecordDTOMapper(ArtistDTOMapper artistDtoMapper) {
        this.artistDtoMapper = artistDtoMapper;
    }

    @Override
    public VinylRecordDTO mapEntityToDTO(VinylRecord vinylRecord) {
        var dto = new VinylRecordDTO();
        dto.setId(vinylRecord.getId());
        dto.setAcquisitionDate(vinylRecord.getAcquisitionDate());
        dto.setGenre(vinylRecord.getGenre());
        dto.setEanCode(vinylRecord.getEanCode());
        dto.setLabel(vinylRecord.getLabel());
        dto.setYearOfRelease(vinylRecord.getYearOfRelease());
        dto.setNumberOfDiscs(vinylRecord.getNumberOfDiscs());
        dto.setTitle(vinylRecord.getTitle());
        dto.setAlbumType(vinylRecord.getAlbumType() != null ? vinylRecord.getAlbumType().name() : null);
        dto.setArtists(vinylRecord.getArtists().stream()
                .map(artistDtoMapper::mapEntityToDTO)
                .collect(Collectors.toSet()));
        dto.setAlbumPhotos(vinylRecord.getAlbumPhotos().stream()
                .sorted(Comparator.comparing(AlbumPhoto::getRank))
                .toList());

        return dto;
    }

    @Override
    public VinylRecordPresentationDTO mapEntityToPresentationDTO(VinylRecord vinylRecord) {
        var dto = new VinylRecordPresentationDTO();
        dto.setId(vinylRecord.getId());
        dto.setAcquisitionDate(vinylRecord.getAcquisitionDate());
        dto.setGenre(vinylRecord.getGenre());
        dto.setEanCode(vinylRecord.getEanCode());
        dto.setYearOfRelease(vinylRecord.getYearOfRelease());
        dto.setNumberOfDiscs(vinylRecord.getNumberOfDiscs());
        dto.setTitle(vinylRecord.getTitle());
        dto.setArtists(vinylRecord.getArtists().stream()
                .map(Artist::getName)
                .toList());

        // Here, it's considered that there would be some logic that imposes there is ONLY ONE cover and ONLY ONE back photo per album, handled at the time of image upload
        var coverPhotoUrl = vinylRecord.getAlbumPhotos().stream()
                .filter(albumPhoto -> AlbumPhotoType.COVER.equals(albumPhoto.getRank()))
                .map(AlbumPhoto::getImageUrl)
                .findFirst().orElse("#");

        dto.setCoverPhotoUrl(coverPhotoUrl);
        return dto;
    }

    @Override
    public VinylRecord mapToEntity(VinylRecordDTO dto) {
        var vinylRecord = new VinylRecord();
        vinylRecord.setTitle(dto.getTitle());
        vinylRecord.setArtists(dto.getArtists().stream()
                .map(artistDtoMapper::mapToEntity)
                .collect(Collectors.toSet()));
        vinylRecord.setAlbumPhotos(dto.getAlbumPhotos());
        vinylRecord.setGenre(dto.getGenre());
        vinylRecord.setEanCode(dto.getEanCode());
        vinylRecord.setYearOfRelease(dto.getYearOfRelease());
        vinylRecord.setNumberOfDiscs(dto.getNumberOfDiscs());
        vinylRecord.setAcquisitionDate(dto.getAcquisitionDate());
        vinylRecord.setLabel(dto.getLabel());
        vinylRecord.setAlbumType(AlbumType.valueOf(dto.getAlbumType()));

        return vinylRecord;
    }
}
