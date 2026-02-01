package com.interview.service;

import com.interview.dto.AlbumDto;
import com.interview.dto.AlbumListDto;
import com.interview.dto.ArtistRefDto;
import com.interview.dto.SongRefDto;
import com.interview.entity.Album;
import com.interview.entity.Artist;
import com.interview.entity.Song;
import com.interview.repository.AlbumRepository;
import com.interview.repository.ArtistRepository;
import com.interview.repository.SongRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, ArtistRepository artistRepository,
                        SongRepository songRepository, ModelMapper modelMapper) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.songRepository = songRepository;
        this.modelMapper = modelMapper;
    }

    public AlbumDto createAlbum(AlbumDto albumDto) {
        Artist artist = artistRepository.findById(albumDto.getArtistId())
                .orElseThrow(() -> new EntityNotFoundException("Artist not found with id: " + albumDto.getArtistId()));

        Album album = new Album();
        album.setTitle(albumDto.getTitle());
        album.setReleaseDate(albumDto.getReleaseDate());
        album.setArtist(artist);

        // Associate with songs if provided
        if (albumDto.getSongIds() != null && !albumDto.getSongIds().isEmpty()) {
            List<Song> songs = songRepository.findAllById(albumDto.getSongIds());
            for (Song song : songs) {
                album.addSong(song);
            }
        }

        Album savedAlbum = albumRepository.save(album);
        return convertToDto(savedAlbum);
    }

    public AlbumDto updateAlbum(Long id, AlbumDto albumDto) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Album not found with id: " + id));

        album.setTitle(albumDto.getTitle());
        album.setReleaseDate(albumDto.getReleaseDate());

        // Update artist if changed
        if (!album.getArtist().getId().equals(albumDto.getArtistId())) {
            Artist newArtist = artistRepository.findById(albumDto.getArtistId())
                    .orElseThrow(() -> new EntityNotFoundException("Artist not found with id: " + albumDto.getArtistId()));
            album.setArtist(newArtist);
        }

        // Update song associations
        if (albumDto.getSongIds() != null) {
            // Remove all current songs (create a copy to avoid ConcurrentModificationException)
            List<Song> currentSongs = new java.util.ArrayList<>(album.getSongs());
            for (Song song : currentSongs) {
                album.removeSong(song);
            }

            // Add new songs
            List<Song> newSongs = songRepository.findAllById(albumDto.getSongIds());
            for (Song song : newSongs) {
                album.addSong(song);
            }
        }

        Album updatedAlbum = albumRepository.save(album);
        return convertToDto(updatedAlbum);
    }

    public void deleteAlbum(Long id) {
        if (!albumRepository.existsById(id)) {
            throw new EntityNotFoundException("Album not found with id: " + id);
        }
        albumRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public AlbumDto getAlbum(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Album not found with id: " + id));
        return convertToDto(album);
    }

    @Transactional(readOnly = true)
    public Page<AlbumListDto> getAllAlbums(Pageable pageable) {
        Page<Album> albums = albumRepository.findAll(pageable);
        return albums.map(this::convertToListDto);
    }

    @Transactional(readOnly = true)
    public Page<AlbumListDto> getAlbumsByArtist(Long artistId, Pageable pageable) {
        if (!artistRepository.existsById(artistId)) {
            throw new EntityNotFoundException("Artist not found with id: " + artistId);
        }
        Page<Album> albums = albumRepository.findByArtistId(artistId, pageable);
        return albums.map(this::convertToListDto);
    }

    @Transactional(readOnly = true)
    public Page<AlbumListDto> searchAlbums(String query, Pageable pageable) {
        Page<Album> albums = albumRepository.findByTitleContainingIgnoreCase(query, pageable);
        return albums.map(this::convertToListDto);
    }

    private AlbumDto convertToDto(Album album) {
        AlbumDto dto = new AlbumDto();
        dto.setId(album.getId());
        dto.setTitle(album.getTitle());
        dto.setReleaseDate(album.getReleaseDate());
        dto.setArtistId(album.getArtist().getId());

        if (album.getSongs() != null) {
            List<Long> songIds = album.getSongs().stream()
                    .map(Song::getId)
                    .collect(Collectors.toList());
            dto.setSongIds(songIds);
        }

        return dto;
    }

    private AlbumListDto convertToListDto(Album album) {
        AlbumListDto dto = new AlbumListDto();
        dto.setId(album.getId());
        dto.setTitle(album.getTitle());
        dto.setReleaseDate(album.getReleaseDate());

        // Set artist reference
        ArtistRefDto artistRef = new ArtistRefDto(album.getArtist().getId(), album.getArtist().getName());
        dto.setArtist(artistRef);

        // Set song count and references
        if (album.getSongs() != null) {
            dto.setSongCount(album.getSongs().size());

            List<SongRefDto> songRefs = album.getSongs().stream()
                    .map(song -> new SongRefDto(song.getId(), song.getTitle()))
                    .collect(Collectors.toList());
            dto.setSongs(songRefs);
        } else {
            dto.setSongCount(0);
        }

        return dto;
    }
}
