package com.interview.service;

import com.interview.domain.VinylRecord;
import com.interview.repository.VinylRecordRepository;
import com.interview.resource.exception.BadRequestException;
import com.interview.resource.exception.EntityNotFoundException;
import com.interview.service.dto.ArtistDTO;
import com.interview.service.dto.VinylRecordDTO;
import com.interview.service.dto.VinylRecordPayloadDTO;
import com.interview.service.dto.VinylRecordPresentationDTO;
import com.interview.service.mapper.VinylRecordDTOMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.List;

import static com.interview.repository.specification.VinylRecordSpecification.byArtist;
import static com.interview.repository.specification.VinylRecordSpecification.byTitle;
import static com.interview.resource.exception.ErrorConstants.*;

@Service
@Transactional
@Validated
public class VinylRecordService {

    private final VinylRecordRepository vinylRecordRepository;
    private final VinylRecordDTOMapper vinylRecordDTOMapper;
    private final ArtistService artistService;

    public VinylRecordService(VinylRecordRepository vinylRecordRepository,
                              VinylRecordDTOMapper vinylRecordDTOMapper,
                              ArtistService artistService) {
        this.vinylRecordRepository = vinylRecordRepository;
        this.vinylRecordDTOMapper = vinylRecordDTOMapper;
        this.artistService = artistService;
    }

    @Transactional(readOnly = true)
    public VinylRecordDTO getVinylRecordById(Long id) {
        var vinylRec = vinylRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ERR_ENTITY_NOT_FOUND));
        return vinylRecordDTOMapper.mapEntityToDTO(vinylRec);
    }

    // FindAll contains a "lightweight" version of the entity that's intended to load fast on a card list that contains all the vinyl records
    @Transactional(readOnly = true)
    public List<VinylRecordPresentationDTO> findAll(String searchQuery, Pageable pageable) {
        return vinylRecordRepository.findAll(byTitle(searchQuery).or(byArtist(searchQuery)), pageable).stream()
                .map(vinylRecordDTOMapper::mapEntityToPresentationDTO)
                .toList();
    }

    // The POST method is used mainly to literally create the entity with title and artists,
    //          and assign a new ID to it, so that the PUT will fill in the blank information
    public Long createNewVinylRecord(VinylRecordPayloadDTO vinylRecordDTO) {
        if (isAlbumAlreadyInserted(vinylRecordDTO.getTitle(), vinylRecordDTO.getArtistIds())) {
            throw new BadRequestException(ERR_RECORD_ALREADY_EXISTS);
        }

        var artists = new HashSet<>(artistService.getAllByIdsIn(vinylRecordDTO.getArtistIds()));
        if (artists.isEmpty()) {
            throw new BadRequestException(ERR_ARTISTS_NOT_FOUND);
        }

        var vinylRecord = new VinylRecord(vinylRecordDTO.getTitle(), artists);
        return vinylRecordRepository.save(vinylRecord).getId();
    }

    public VinylRecordDTO updateVinylRecord(Long id, VinylRecordDTO vinylRecordDTO) {
        if (id == null) {
            throw new BadRequestException(ERR_INVALID_RECORD_ID);
        }

        var vinylRecord = vinylRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ERR_ENTITY_NOT_FOUND));

        var artistEntities = new HashSet<>(artistService.getAllByIdsIn(vinylRecordDTO.getArtists().stream()
                .map(ArtistDTO::getId)
                .toList()));

        if (artistEntities.isEmpty()) {
            throw new BadRequestException(ERR_ARTISTS_NOT_FOUND);
        }

        vinylRecord.setFieldsFromDTO(vinylRecordDTO, artistEntities);

        return vinylRecordDTOMapper.mapEntityToDTO(vinylRecordRepository.save(vinylRecord));
    }

    public void deleteVinylRecordById(Long id) {
        if (id == null) {
            throw new BadRequestException(ERR_INVALID_RECORD_ID);
        }

        var vinylRecord = vinylRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ERR_ENTITY_NOT_FOUND));

        vinylRecordRepository.delete(vinylRecord);
    }

    @Transactional(readOnly = true)
    public boolean isAlbumAlreadyInserted(String title, List<Long> artistIds) {
        return vinylRecordRepository.countAlbumsAlreadyInsertedWithSameCombination(title, artistIds) != 0;
    }
}
