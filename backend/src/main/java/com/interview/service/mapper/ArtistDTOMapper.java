package com.interview.service.mapper;

import com.interview.domain.Artist;
import com.interview.service.ArtistInformationService;
import com.interview.service.dto.ArtistDTO;
import org.springframework.stereotype.Service;

@Service
public class ArtistDTOMapper implements GenericMapper<Artist, ArtistDTO, String>{

    private final ArtistInformationService artistInformationService;

    public ArtistDTOMapper(ArtistInformationService artistInformationService) {
        this.artistInformationService = artistInformationService;
    }

    @Override
    public Artist mapToEntity(ArtistDTO artistDTO) {
        var artist = new Artist();
        artist.setId(artistDTO.getId());
        artist.setName(artistDTO.getName());
        artist.setImageUrl(artistDTO.getImageUrl());
        return artist;
    }

    @Override
    public ArtistDTO mapEntityToDTO(Artist artist) {
        var artistDTO = new ArtistDTO();
        artistDTO.setId(artist.getId());
        artistDTO.setName(artist.getName());
        artistDTO.setImageUrl(artist.getImageUrl());
        artistDTO.setDescription(artistInformationService.getArtistInformation(artist.getName()));
        return artistDTO;
    }

    @Override
    public String mapEntityToPresentationDTO(Artist artist) {
        return artist.getName();
    }
}
