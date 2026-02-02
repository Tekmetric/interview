package com.interview.service;

import com.interview.dto.ArtistDto;
import com.interview.dto.ArtistListDto;
import com.interview.dto.EntityType;
import com.interview.dto.NotificationAction;
import com.interview.entity.Artist;
import com.interview.event.EntityChangeEvent;
import com.interview.repository.ArtistRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ArtistService(ArtistRepository artistRepository, ModelMapper modelMapper, ApplicationEventPublisher eventPublisher) {
        this.artistRepository = artistRepository;
        this.modelMapper = modelMapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ArtistDto createArtist(ArtistDto artistDto) {
        Artist artist = modelMapper.map(artistDto, Artist.class);
        Artist savedArtist = artistRepository.save(artist);
        eventPublisher.publishEvent(new EntityChangeEvent(NotificationAction.CREATE, EntityType.ARTIST, savedArtist.getId()));
        return modelMapper.map(savedArtist, ArtistDto.class);
    }

    @Transactional
    public ArtistDto updateArtist(Long id, ArtistDto artistDto) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artist not found with id: " + id));

        artist.setName(artistDto.getName());
        Artist updatedArtist = artistRepository.save(artist);
        eventPublisher.publishEvent(new EntityChangeEvent(NotificationAction.UPDATE, EntityType.ARTIST, updatedArtist.getId()));
        return modelMapper.map(updatedArtist, ArtistDto.class);
    }

    @Transactional
    public void deleteArtist(Long id) {
        if (!artistRepository.existsById(id)) {
            throw new EntityNotFoundException("Artist not found with id: " + id);
        }
        artistRepository.deleteById(id);
        eventPublisher.publishEvent(new EntityChangeEvent(NotificationAction.DELETE, EntityType.ARTIST, id));
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
        return modelMapper.map(artist, ArtistListDto.class);
    }
}
