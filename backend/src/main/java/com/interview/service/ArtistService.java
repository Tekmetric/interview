package com.interview.service;

import com.interview.dto.ArtistDto;
import com.interview.dto.ArtistListDto;
import com.interview.entity.Artist;
import com.interview.repository.ArtistRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ArtistService(ArtistRepository artistRepository, ModelMapper modelMapper) {
        this.artistRepository = artistRepository;
        this.modelMapper = modelMapper;
    }

    public ArtistDto createArtist(ArtistDto artistDto) {
        Artist artist = modelMapper.map(artistDto, Artist.class);
        Artist savedArtist = artistRepository.save(artist);
        return modelMapper.map(savedArtist, ArtistDto.class);
    }

    public ArtistDto updateArtist(Long id, ArtistDto artistDto) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artist not found with id: " + id));

        artist.setName(artistDto.getName());
        Artist updatedArtist = artistRepository.save(artist);
        return modelMapper.map(updatedArtist, ArtistDto.class);
    }

    public void deleteArtist(Long id) {
        if (!artistRepository.existsById(id)) {
            throw new EntityNotFoundException("Artist not found with id: " + id);
        }
        artistRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ArtistDto getArtist(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artist not found with id: " + id));
        return modelMapper.map(artist, ArtistDto.class);
    }

    @Transactional(readOnly = true)
    public Page<ArtistListDto> getAllArtists(Pageable pageable) {
        Page<Artist> artists = artistRepository.findAll(pageable);
        return artists.map(this::convertToListDto);
    }

    @Transactional(readOnly = true)
    public Page<ArtistListDto> searchArtists(String query, Pageable pageable) {
        Page<Artist> artists = artistRepository.findByNameContainingIgnoreCase(query, pageable);
        return artists.map(this::convertToListDto);
    }

    private ArtistListDto convertToListDto(Artist artist) {
        ArtistListDto dto = new ArtistListDto();
        dto.setId(artist.getId());
        dto.setName(artist.getName());
        dto.setSongCount(artist.getSongs() != null ? artist.getSongs().size() : 0);
        dto.setAlbumCount(artist.getAlbums() != null ? artist.getAlbums().size() : 0);
        return dto;
    }
}
