package com.interview.service;

import com.interview.constant.MetricsConstants;
import com.interview.dto.WishDTO;
import com.interview.dto.WishLightDTO;
import com.interview.exception.WishNotFoundException;
import com.interview.model.Wish;
import com.interview.repository.WishRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishServiceTest {

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    @Mock
    private WishRepository wishRepository;

    private WishService wishService;

    private Wish wish;
    private WishDTO wishDTO;

    @BeforeEach
    void setUp() {
        wish = new Wish();
        wish.setId(1L);
        wish.setName("Test Wish");
        wish.setComment("Test Comment");
        wish.setLink("http://test.com");
        wish.setCameTrue(false);
        wish.setDeleted(false);
        wish.setCreatedAt(LocalDateTime.now());
        wish.setUpdatedAt(LocalDateTime.now());

        wishDTO = new WishDTO();
        wishDTO.setName("Test Wish");
        wishDTO.setComment("Test Comment");
        wishDTO.setLink("http://test.com");
        wishDTO.setCreatedAt(wish.getCreatedAt());
        wishDTO.setUpdatedAt(wish.getUpdatedAt());

        when(meterRegistry.counter(MetricsConstants.WISHES_CAME_TRUE_COUNT)).thenReturn(counter);
        
        wishService = new WishService(wishRepository, meterRegistry);
    }

    @Test
    void getAllWishes_Paginated_ShouldReturnLightweightDTOs() {
        // Arrange
        Wish wish2 = new Wish();
        wish2.setName("Wish 2");
        wish2.setCameTrue(true);
        List<Wish> wishes = Arrays.asList(wish, wish2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Wish> wishPage = new PageImpl<>(wishes, pageable, 2);
        
        when(wishRepository.findAllByDeletedFalse(pageable)).thenReturn(wishPage);

        // Act
        Page<WishLightDTO> result = wishService.getAllWishes(pageable);

        // Assert
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Wish");
        assertThat(result.getContent().get(0).isCameTrue()).isFalse();
        assertThat(result.getContent().get(1).getName()).isEqualTo("Wish 2");
        assertThat(result.getContent().get(1).isCameTrue()).isTrue();
        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(wishRepository, times(1)).findAllByDeletedFalse(pageable);
    }

    @Test
    void getWishById_WhenFound_ShouldReturnFullDTO() {
        // Arrange
        when(wishRepository.findById(1L)).thenReturn(Optional.of(wish));

        // Act
        WishDTO result = wishService.getWishById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Wish");
        verify(wishRepository, times(1)).findById(1L);
    }

    @Test
    void getWishById_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(wishRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> wishService.getWishById(1L))
                .isInstanceOf(WishNotFoundException.class)
                .hasMessageContaining("Wish not found with id: 1");
    }

    @Test
    void getWishById_WhenDeleted_ShouldThrowException() {
        // Arrange
        wish.setDeleted(true);
        when(wishRepository.findById(1L)).thenReturn(Optional.of(wish));

        // Act & Assert
        assertThatThrownBy(() -> wishService.getWishById(1L))
                .isInstanceOf(WishNotFoundException.class)
                .hasMessageContaining("Wish not found with id: 1");
    }

    @Test
    void createWish_ShouldSaveAndReturnDTO() {
        // Arrange
        when(wishRepository.save(any(Wish.class))).thenAnswer(invocation -> {
            Wish savedWish = invocation.getArgument(0);
            savedWish.setId(1L);
            return savedWish;
        });

        // Act
        WishDTO result = wishService.createWish(wishDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Wish");
        verify(wishRepository, times(1)).save(any(Wish.class));
    }

    @Test
    void updateWish_ShouldUpdateFieldsAndReturnDTO() {
        // Arrange
        when(wishRepository.findById(1L)).thenReturn(Optional.of(wish));
        when(wishRepository.save(any(Wish.class))).thenReturn(wish);

        WishDTO updateDTO = new WishDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setComment("Updated Comment");
        updateDTO.setLink("http://updated.com");

        // Act
        WishDTO result = wishService.updateWish(1L, updateDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(wish.getName()).isEqualTo("Updated Name");
        verify(wishRepository, times(1)).findById(1L);
        verify(wishRepository, times(1)).save(wish);
    }

    @Test
    void deleteWish_ShouldSetDeletedFlag() {
        // Arrange
        when(wishRepository.findById(1L)).thenReturn(Optional.of(wish));

        // Act
        wishService.deleteWish(1L);

        // Assert
        assertThat(wish.isDeleted()).isTrue();
        verify(wishRepository, times(1)).findById(1L);
        verify(wishRepository, times(1)).save(wish);
    }

    @Test
    void markAsCameTrue_ShouldSetCameTrueFlag() {
        // Arrange
        when(wishRepository.findById(1L)).thenReturn(Optional.of(wish));
        when(wishRepository.save(any(Wish.class))).thenReturn(wish);

        // Act
        WishDTO result = wishService.markAsCameTrue(1L);

        // Assert
        assertThat(result.isCameTrue()).isTrue();
        assertThat(wish.isCameTrue()).isTrue();
        verify(wishRepository, times(1)).findById(1L);
        verify(wishRepository, times(1)).save(wish);
    }
}
