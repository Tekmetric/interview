package com.interview.service;

import com.interview.domain.Artist;
import com.interview.repository.ArtistRepository;
import com.interview.resource.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Transactional(readOnly = true)
    public List<Artist> getAllByIdsIn(List<Long> artistIds) {
        if (artistIds == null || artistIds.isEmpty()) {
            throw new BadRequestException("Please provide at least one artist");
        }
        return artistRepository.findAllById(artistIds);
    }
}
