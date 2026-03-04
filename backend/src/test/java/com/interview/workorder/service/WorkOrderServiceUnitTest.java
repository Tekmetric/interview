package com.interview.workorder.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.interview.common.error.ResourceNotFoundException;
import com.interview.customer.entity.Customer;
import com.interview.customer.service.CustomerService;
import com.interview.workorder.dao.WorkOrderRepository;
import com.interview.workorder.entity.WorkOrder;
import com.interview.workorder.mapping.WorkOrderMapper;
import com.interview.workorder.request.WorkOrderRequest;
import com.interview.workorder.response.WorkOrderResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class WorkOrderServiceUnitTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private WorkOrderRepository repository;

    private WorkOrderMapper mapper;
    private WorkOrderService service;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(WorkOrderMapper.class);
        service = new WorkOrderService(customerService, repository, mapper);
    }

    @Test
    void createShouldPersistWorkOrderForGivenCustomer() {
        Customer customer = customer(1L, "Alice Johnson");
        WorkOrderRequest request = new WorkOrderRequest(
                "1HGCM82633A004352",
                "Brake pads replacement",
                "OPEN"
        );

        when(customerService.findByIdOrThrow(1L)).thenReturn(customer);
        when(repository.save(any(WorkOrder.class))).thenAnswer(invocation -> {
            WorkOrder workOrder = invocation.getArgument(0);
            workOrder.setId(10L);
            return workOrder;
        });

        WorkOrderResponse response = service.create(1L, request);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.customerId()).isEqualTo(1L);
        assertThat(response.vin()).isEqualTo("1HGCM82633A004352");
        assertThat(response.status()).isEqualTo("OPEN");

        ArgumentCaptor<WorkOrder> captor = ArgumentCaptor.forClass(WorkOrder.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getCustomer()).isSameAs(customer);
        assertThat(captor.getValue().getIssueDescription()).isEqualTo("Brake pads replacement");
    }

    @Test
    void listShouldLoadByCustomerIdAndAscIdSort() {
        Customer customer = customer(1L, "Alice Johnson");
        WorkOrder first = workOrder(1L, customer, "1HGCM82633A004352", "OPEN");
        WorkOrder second = workOrder(2L, customer, "JH4KA9650MC012345", "IN_PROGRESS");

        when(customerService.findByIdOrThrow(1L)).thenReturn(customer);
        when(repository.findAllByCustomer_Id(eq(1L), any(Sort.class))).thenReturn(List.of(first, second));

        List<WorkOrderResponse> responses = service.list(1L);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).id()).isEqualTo(1L);
        assertThat(responses.get(0).customerId()).isEqualTo(1L);
        assertThat(responses.get(1).status()).isEqualTo("IN_PROGRESS");

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(repository).findAllByCustomer_Id(eq(1L), sortCaptor.capture());
        Sort.Order order = sortCaptor.getValue().getOrderFor("id");
        assertThat(order).isNotNull();
        assertThat(order.isAscending()).isTrue();
    }

    @Test
    void getByIdShouldThrowWhenWorkOrderDoesNotBelongToCustomer() {
        Customer customer = customer(1L, "Alice Johnson");
        when(customerService.findByIdOrThrow(1L)).thenReturn(customer);
        when(repository.findByIdAndCustomer_Id(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("WorkOrder with id 99 was not found");
    }

    @Test
    void updateShouldModifyMutableFields() {
        Customer customer = customer(1L, "Alice Johnson");
        WorkOrder existing = workOrder(5L, customer, "1HGCM82633A004352", "OPEN");
        existing.setIssueDescription("Initial issue");

        WorkOrderRequest request = new WorkOrderRequest(
                "1HGCM82633A004352",
                "Issue resolved",
                "COMPLETED"
        );

        when(customerService.findByIdOrThrow(1L)).thenReturn(customer);
        when(repository.findByIdAndCustomer_Id(5L, 1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(WorkOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WorkOrderResponse response = service.update(1L, 5L, request);

        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.customerId()).isEqualTo(1L);
        assertThat(response.issueDescription()).isEqualTo("Issue resolved");
        assertThat(response.status()).isEqualTo("COMPLETED");
    }

    @Test
    void deleteShouldRemoveOwnedWorkOrder() {
        Customer customer = customer(1L, "Alice Johnson");
        WorkOrder existing = workOrder(7L, customer, "1HGCM82633A004352", "OPEN");

        when(customerService.findByIdOrThrow(1L)).thenReturn(customer);
        when(repository.findByIdAndCustomer_Id(7L, 1L)).thenReturn(Optional.of(existing));

        service.delete(1L, 7L);

        verify(repository).delete(existing);
    }

    private static Customer customer(Long id, String name) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        return customer;
    }

    private static WorkOrder workOrder(Long id, Customer customer, String vin, String status) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(id);
        workOrder.setCustomer(customer);
        workOrder.setVin(vin);
        workOrder.setIssueDescription("Issue");
        workOrder.setStatus(status);
        return workOrder;
    }
}
