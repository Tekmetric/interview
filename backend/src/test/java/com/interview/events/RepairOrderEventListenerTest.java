package com.interview.events;

import com.interview.dto.RepairOrderUpdateRequest;
import com.interview.entity.RepairOrderStatus;
import com.interview.service.RepairOrderService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RepairOrderEventListenerTest {

    @Autowired
    private RepairOrderService service;

    @SpyBean
    private RepairOrderEventListener listener;

    @Test
    void testEventPublishedWhenOrderCompletedAndCommitted() {
        // We assume order id=1 exists in test DB and is not COMPLETED yet (see src/main/resources/data.sql).
        // A more realistic test might create the repair order first or use test containers,
        // but for this demo we can just rely on the pre-seeded data done during app start-up.
        RepairOrderUpdateRequest req = new RepairOrderUpdateRequest(null, null, RepairOrderStatus.COMPLETED, null);

        service.update(1L, req); // transactional; event will be emitted

        // Verify listener received the event (AFTER_COMMIT)
        ArgumentCaptor<RepairOrderCompletedEvent> captor = ArgumentCaptor.forClass(RepairOrderCompletedEvent.class);

        verify(listener, timeout(1000)).onRepairOrderCompleted(captor.capture());

        RepairOrderCompletedEvent event = captor.getValue();
        assertThat(event.repairOrderId()).isEqualTo(1L);
        assertThat(event.customerName()).isNotBlank();
    }
}
