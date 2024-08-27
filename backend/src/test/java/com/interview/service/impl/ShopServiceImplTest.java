package com.interview.service.impl;

import com.interview.api.dto.DetailViewShopDTO;
import com.interview.exception.ServiceException;
import com.interview.model.Shop;
import com.interview.repository.ShopRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static com.interview.exception.ExceptionReason.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@SpringBootTest
class ShopServiceImplTest {

    @Mock
    private ShopRepository shopRepository;

    @InjectMocks
    private ShopServiceImpl shopService;

    @BeforeAll
    public static void setUp() {
        openMocks(ShopServiceImplTest.class);
    }

    @Test
    public void testGetShopById() {
        long shopId = 1L;
        Shop sampleShop = createSampleShop(shopId);
        when(shopRepository.findByIdAndActiveTrue(shopId))
                .thenReturn(Optional.of(sampleShop));

        DetailViewShopDTO shopRetrievedById = shopService.findById(shopId);

        assertNotNull(shopRetrievedById);
        assertEquals(sampleShop.getName(), shopRetrievedById.getName());
        assertEquals(sampleShop.getAddress(), shopRetrievedById.getAddress());
        assertEquals(sampleShop.getPhoneNo(), shopRetrievedById.getPhoneNo());
    }

    @Test
    public void testGetShopByIdNotFound() {
        long shopId = 1L;
        when(shopRepository.findByIdAndActiveTrue(shopId))
                .thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class, () -> shopService.findById(shopId));
        assertEquals(NOT_FOUND, serviceException.getReason());
        assertEquals("Invalid id", serviceException.getMessage());
    }

    private Shop createSampleShop(long id) {
        return new Shop(id, "Sample name", "Sample desc", "Sample address", "Sample phoneNo", "Sample email", true);
    }

}