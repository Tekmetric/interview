package com.interview.service;

import com.interview.dto.AlbumRefDto;
import com.interview.dto.ArtistRefDto;
import com.interview.dto.EntityType;
import com.interview.dto.NotificationAction;
import com.interview.dto.SongDto;
import com.interview.dto.SongListDto;
import com.interview.entity.Album;
import com.interview.entity.Artist;
import com.interview.entity.Song;
import com.interview.event.EntityChangeEvent;
import com.interview.repository.AlbumRepository;
import com.interview.repository.ArtistRepository;
import com.interview.repository.SongRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SongService {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public SongService(SongRepository songRepository, ArtistRepository artistRepository,
                       AlbumRepository albumRepository, ModelMapper modelMapper, ApplicationEventPublisher eventPublisher) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.modelMapper = modelMapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public SongDto createSong(SongDto songDto) {
        // Map basic properties using ModelMapper
        Song song = modelMapper.map(songDto, Song.class);

        // Handle artist relationship
        Artist artist = artistRepository.findById(songDto.getArtistId())
                .orElseThrow(() -> new EntityNotFoundException("Artist not found with id: " + songDto.getArtistId()));
        song.setArtist(artist);

        // Associate with albums if provided - delegate to entity to manage both sides
        if (songDto.getAlbumIds() != null && !songDto.getAlbumIds().isEmpty()) {
            List<Album> albums = albumRepository.findAllById(songDto.getAlbumIds());
            song.setAlbums(albums);
        }

        Song savedSong = songRepository.save(song);
        eventPublisher.publishEvent(new EntityChangeEvent(NotificationAction.CREATE, EntityType.SONG, savedSong.getId()));
        return convertToDto(savedSong);
    }

    @Transactional
    public SongDto updateSong(Long id, SongDto songDto) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Song not found with id: " + id));

        // Map basic properties using ModelMapper
        modelMapper.map(songDto, song);

        // Update artist if changed
        if (!song.getArtist().getId().equals(songDto.getArtistId())) {
            Artist newArtist = artistRepository.findById(songDto.getArtistId())
                    .orElseThrow(() -> new EntityNotFoundException("Artist not found with id: " + songDto.getArtistId()));
            song.setArtist(newArtist);
        }

        // Update album associations - delegate to entity to manage both sides
        if (songDto.getAlbumIds() != null) {
            List<Album> newAlbums = albumRepository.findAllById(songDto.getAlbumIds());
            song.setAlbums(newAlbums);
        }

        Song updatedSong = songRepository.save(song);
        eventPublisher.publishEvent(new EntityChangeEvent(NotificationAction.UPDATE, EntityType.SONG, updatedSong.getId()));
        return convertToDto(updatedSong);
    }

    @Transactional
    public void deleteSong(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Song not found with id: " + id));

        // Remove from all albums before deleting
        song.setAlbums(null);
        songRepository.delete(song);
        eventPublisher.publishEvent(new EntityChangeEvent(NotificationAction.DELETE, EntityType.SONG, id));
    }

    @Transactional(readOnly = true)
    public SongDto getSong(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Song not found with id: " + id));
        return convertToDto(song);
    }

    @Transactional(readOnly = true)
    public Page<SongListDto> getAllSongs(Pageable pageable) {
        Page<Song> songs = songRepository.findAll(pageable);
        return songs.map(this::convertToListDto);
    }

    @Transactional(readOnly = true)
    public Page<SongListDto> getSongsByArtist(Long artistId, Pageable pageable) {
        if (!artistRepository.existsById(artistId)) {
            throw new EntityNotFoundException("Artist not found with id: " + artistId);
        }
        Page<Song> songs = songRepository.findByArtistId(artistId, pageable);
        return songs.map(this::convertToListDto);
    }

    @Transactional(readOnly = true)
    public Page<SongListDto> getSongsByAlbum(Long albumId, Pageable pageable) {
        if (!albumRepository.existsById(albumId)) {
            throw new EntityNotFoundException("Album not found with id: " + albumId);
        }
        Page<Song> songs = songRepository.findByAlbumsId(albumId, pageable);
        return songs.map(this::convertToListDto);
    }

    @Transactional(readOnly = true)
    public Page<SongListDto> searchSongs(String query, Pageable pageable) {
        Page<Song> songs = songRepository.findByTitleContainingIgnoreCase(query, pageable);
        return songs.map(this::convertToListDto);
    }

    private SongDto convertToDto(Song song) {
        // Map basic properties using ModelMapper
        SongDto dto = modelMapper.map(song, SongDto.class);

        // Set artist ID (relationship field)
        dto.setArtistId(song.getArtist().getId());

        // Set album IDs (relationship field)
        if (song.getAlbums() != null) {
            List<Long> albumIds = song.getAlbums().stream()
                    .map(Album::getId)
                    .collect(Collectors.toList());
            dto.setAlbumIds(albumIds);
        }

        return dto;
    }

    private SongListDto convertToListDto(Song song) {
        SongListDto dto = modelMapper.map(song, SongListDto.class);

        // Set artist reference
        ArtistRefDto artistRef = new ArtistRefDto(song.getArtist().getId(), song.getArtist().getName());
        dto.setArtist(artistRef);

        // Set album references
        if (song.getAlbums() != null) {
            List<AlbumRefDto> albumRefs = song.getAlbums().stream()
                    .map(album -> modelMapper.map(album, AlbumRefDto.class))
                    .collect(Collectors.toList());
            dto.setAlbums(albumRefs);
        }

        return dto;
    }
}
