package com.interview.service;

import com.interview.domain.Artist;
import com.interview.domain.VinylRecord;
import com.interview.repository.VinylRecordRepository;
import com.interview.resource.exception.BadRequestException;
import com.interview.resource.exception.ErrorConstants;
import com.interview.service.ArtistService;
import com.interview.service.VinylRecordService;
import com.interview.service.dto.VinylRecordPayloadDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class VinylRecordServiceTest {


    public static final String TEST_ALBUM_NAME = "My Really fancy album, that's a mix of jazz and turbo folk";
    public static final List<Long> TEST_ARTIST_IDS = List.of(1L, 2L);

    @Mock
    private ArtistService artistService;

    @Mock
    private VinylRecordRepository vinylRecordRepository;

    @InjectMocks
    private VinylRecordService vinylRecordService;

    private VinylRecordPayloadDTO validVinylRecordDTO;
    private VinylRecordPayloadDTO invalidVinylRecordDTO;
    private List<Artist> artistList;

    @BeforeEach
    void setUp() {
        openMocks(this);

        validVinylRecordDTO = new VinylRecordPayloadDTO();
        validVinylRecordDTO.setTitle(TEST_ALBUM_NAME);
        validVinylRecordDTO.setArtistIds(TEST_ARTIST_IDS);

        invalidVinylRecordDTO = new VinylRecordPayloadDTO();
        artistList = createArtists();

    }

//    @Test
//    void whenTitleIsBlank_thenThrowBadRequestException() {
//        invalidVinylRecordDTO.setArtistIds(TEST_ARTIST_IDS);
//
//        var exception = assertThrows(BadRequestException.class,
//                () -> vinylRecordService.createNewVinylRecord(invalidVinylRecordDTO));
//
//        assertEquals(ErrorConstants.ERR_VR_TITLE_IS_REQUIRED, exception.getMessage());
//    }
//
//    @Test
//    void whenArtistIdsAreNull_thenThrowBadRequestException() {
//        invalidVinylRecordDTO.setTitle(TEST_ALBUM_NAME);
//
//        var exception = assertThrows(BadRequestException.class,
//                () -> vinylRecordService.createNewVinylRecord(invalidVinylRecordDTO));
//
//        assertEquals(ErrorConstants.ERR_ARTISTS_ARE_REQUIRED, exception.getMessage());
//    }
//
//    @Test
//    void whenArtistIdsAreEmpty_thenThrowBadRequestException() {
//        invalidVinylRecordDTO.setTitle(TEST_ALBUM_NAME);
//        invalidVinylRecordDTO.setArtistIds(Collections.emptyList());
//
//        BadRequestException exception = assertThrows(BadRequestException.class,
//                () -> vinylRecordService.createNewVinylRecord(invalidVinylRecordDTO));
//
//        assertEquals(ErrorConstants.ERR_ARTISTS_ARE_REQUIRED, exception.getMessage());
//    }

    @Test
    void whenRecordAlreadyExists_thenThrowBadRequestException() {
        when(vinylRecordRepository.countAlbumsAlreadyInsertedWithSameCombination(anyString(), anyList())).thenReturn(1);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> vinylRecordService.createNewVinylRecord(validVinylRecordDTO));

        assertEquals(ErrorConstants.ERR_RECORD_ALREADY_EXISTS, exception.getMessage());
    }

    @Test
    void whenArtistsNotFound_thenThrowBadRequestException() {
        when(vinylRecordRepository.countAlbumsAlreadyInsertedWithSameCombination(anyString(), anyList())).thenReturn(0);
        when(artistService.getAllByIdsIn(anyList()))
                .thenReturn(Collections.emptyList());

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> vinylRecordService.createNewVinylRecord(validVinylRecordDTO));

        assertEquals(ErrorConstants.ERR_ARTISTS_NOT_FOUND, exception.getMessage());
    }

    @Test
    void whenValidVinylRecord_thenSaveAndReturnId() {
        VinylRecord vinylRecord = new VinylRecord(TEST_ALBUM_NAME, new HashSet<>());
        vinylRecord.setId(123L);

        when(vinylRecordRepository.countAlbumsAlreadyInsertedWithSameCombination(anyString(), anyList())).thenReturn(0);
        when(artistService.getAllByIdsIn(anyList()))
                .thenReturn(artistList);
        when(vinylRecordRepository.save(any(VinylRecord.class)))
                .thenReturn(vinylRecord);

        var id = vinylRecordService.createNewVinylRecord(validVinylRecordDTO);

        assertNotNull(id);
        assertEquals(123L, id);
    }


    @Test
    void whenAlbumAlreadyInserted_thenReturnTrue() {
        when(vinylRecordRepository.countAlbumsAlreadyInsertedWithSameCombination(TEST_ALBUM_NAME, TEST_ARTIST_IDS)).thenReturn(1);

        var result = vinylRecordService.isAlbumAlreadyInserted(TEST_ALBUM_NAME, TEST_ARTIST_IDS);

        assertTrue(result);
    }

    @Test
    void whenAlbumNotAlreadyInserted_thenReturnFalse() {
        when(vinylRecordRepository.countAlbumsAlreadyInsertedWithSameCombination(TEST_ALBUM_NAME, TEST_ARTIST_IDS)).thenReturn(0);

        var result = vinylRecordService.isAlbumAlreadyInserted(TEST_ALBUM_NAME, TEST_ARTIST_IDS);

        assertFalse(result);
    }

    @TestConfiguration
    static class TestContextConfiguration {
        @Bean
        public MethodValidationPostProcessor bean() {
            return new MethodValidationPostProcessor();
        }
    }

    private List<Artist> createArtists() {
        var a1 = new Artist();
        a1.setId(1L);
        a1.setName("Bon Jovi");
        var a2 = new Artist();
        a2.setId(2L);
        a2.setName("Aerosmith");
        return List.of(a1, a2);
    }
}