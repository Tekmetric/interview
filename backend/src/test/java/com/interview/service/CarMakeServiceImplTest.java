package com.interview.service;

import com.interview.dto.CarMakeCreateDto;
import com.interview.dto.CarMakeDto;
import com.interview.dto.CarMakeUpdateDto;
import com.interview.mapper.CarMakeMapper;
import com.interview.model.CarMake;
import com.interview.model.CarMakeNotFoundException;
import com.interview.repository.CarMakeRepository;
import com.interview.service.impl.CarMakeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class CarMakeServiceImplTest {

    @Mock
    private CarMakeRepository repository;

    @InjectMocks
    private CarMakeServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void create_success() {
        CarMakeCreateDto createDto = new CarMakeCreateDto("Toyota", "Japan", 1937);
        CarMake entityToSave = CarMakeMapper.fromCreateDto(createDto);
        CarMake savedEntity = new CarMake(1L, entityToSave.getName(), entityToSave.getCountry(), entityToSave.getFoundedYear());

        when(repository.save(any(CarMake.class))).thenReturn(savedEntity);

        CarMakeDto result = service.create(createDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Toyota", result.getName());
        assertEquals("Japan", result.getCountry());
        assertEquals(1937, result.getFoundedYear());

        ArgumentCaptor<CarMake> captor = ArgumentCaptor.forClass(CarMake.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
    }

    @Test
    void create_duplicateName_illegalArgument() {
        CarMakeCreateDto createDto = new CarMakeCreateDto("Toyota", "Japan", 1937);
        when(repository.save(any(CarMake.class))).thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThrows(IllegalArgumentException.class, () -> service.create(createDto));
    }

    @Test
    void getById_success() {
        CarMake entity = new CarMake(5L, "Ford", "USA", 1903);
        when(repository.findById(5L)).thenReturn(Optional.of(entity));

        CarMakeDto dto = service.getById(5L);

        assertEquals(5L, dto.getId());
        assertEquals("Ford", dto.getName());
        assertEquals("USA", dto.getCountry());
        assertEquals(1903, dto.getFoundedYear());
    }

    @Test
    void getById_notFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CarMakeNotFoundException.class, () -> service.getById(99L));
    }

    @Test
    void getAll_noFilters() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        CarMake e1 = new CarMake(1L, "Audi", "Germany", 1909);
        CarMake e2 = new CarMake(2L, "Volvo", "Sweden", 1927);
        Page<CarMake> page = new PageImpl<>(Arrays.asList(e1, e2), pageable, 2);
        when(repository.findAll(pageable)).thenReturn(page);

        Page<CarMakeDto> result = service.getAll(null, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("Audi", result.getContent().get(0).getName());
        assertEquals("Volvo", result.getContent().get(1).getName());
    }

    @Test
    void getAll_withFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        CarMake e1 = new CarMake(1L, "Toyota", "Japan", 1937);
        Page<CarMake> page = new PageImpl<>(Collections.singletonList(e1), pageable, 1);
        when(repository.findByNameContainingIgnoreCase("toy", pageable)).thenReturn(page);

        Page<CarMakeDto> result = service.getAll("toy", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Toyota", result.getContent().get(0).getName());
    }

    @Test
    void update_success() {
        Long id = 10L;
        CarMake existing = new CarMake(id, "OldName", "OldCountry", 1900);
        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any(CarMake.class))).thenAnswer(inv -> inv.getArgument(0));

        CarMakeUpdateDto updateDto = new CarMakeUpdateDto();
        updateDto.setName("NewName");
        updateDto.setCountry("NewCountry");
        updateDto.setFoundedYear(2000);

        CarMakeDto result = service.update(id, updateDto);

        assertEquals("NewName", result.getName());
        assertEquals("NewCountry", result.getCountry());
        assertEquals(2000, result.getFoundedYear());
        verify(repository).save(existing);
    }

    @Test
    void update_notFound() {
        Long id = 42L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CarMakeNotFoundException.class, () -> service.update(id, new CarMakeUpdateDto()));
    }

    @Test
    void update_duplicate_illegalArgument() {
        Long id = 3L;
        CarMake existing = new CarMake(id, "Nissan", "Japan", 1933);
        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any(CarMake.class))).thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThrows(IllegalArgumentException.class, () -> service.update(id, new CarMakeUpdateDto()));
    }

    @Test
    void delete_success() {
        Long id = 7L;
        CarMake existing = new CarMake(id, "Mazda", "Japan", 1920);
        when(repository.findById(id)).thenReturn(Optional.of(existing));

        service.delete(id);

        verify(repository).delete(existing);
    }

    @Test
    void delete_notFound() {
        when(repository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(CarMakeNotFoundException.class, () -> service.delete(100L));
    }
}
