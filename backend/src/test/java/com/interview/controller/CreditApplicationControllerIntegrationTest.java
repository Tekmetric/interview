package com.interview.controller;

import static com.interview.util.TestFixtures.APPLICATION_ID;
import static com.interview.util.TestFixtures.CUSTOMER_ID;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.interview.persistence.enums.ApplicationStatus;
import com.interview.persistence.enums.EmploymentStatus;
import com.interview.persistence.entity.Customer;
import com.interview.persistence.entity.embedded.EmploymentDetails;
import com.interview.exception.CreditApplicationNotFoundException;
import com.interview.exception.CustomerNotFoundException;
import com.interview.exception.InvalidApplicationStateException;
import com.interview.service.CreditApplicationService;
import com.interview.util.TestFixtureLoader;
import com.interview.util.TestFixtures;

@WebMvcTest(CreditApplicationController.class)
class CreditApplicationControllerIntegrationTest extends BaseIntegrationTest {

    private static final String ENDPOINT = "/api/v1/credit-applications";

    @Override
    protected String protectedEndpoint() {
        return ENDPOINT;
    }

    @MockitoBean
    CreditApplicationService creditApplicationService;

    @Test
    void create_validRequest_returns201WithLocation() throws Exception {
        when(creditApplicationService.create(any()))
                .thenReturn(TestFixtures.creditApplicationResponseSubmitted());

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestFixtureLoader.load("credit_application/create_credit_application_request.json")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(ENDPOINT)))
                .andExpect(content().json(
                        TestFixtureLoader.load("credit_application/credit_application_response_submitted.json")));
    }

    @Test
    void create_missingCustomerId_returns400WithFieldError() throws Exception {
        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestFixtureLoader.load(
                                "credit_application/create_credit_application_request_missing_customer_id.json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.customerId").exists());

        verifyNoInteractions(creditApplicationService);
    }

    @Test
    void create_loanAmountExceedsLimit_returns400WithGlobalError() throws Exception {
        when(customerRepository.findById(CUSTOMER_ID))
                .thenReturn(Optional.of(customerWithAnnualIncome("1000.00")));

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestFixtureLoader.load(
                                "credit_application/create_credit_application_request_exceeds_loan_limit.json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isMap())
                .andExpect(jsonPath("$.globalErrors[0]")
                        .value("Requested loan amount exceeds the maximum allowed (5× annual income)"));

        verifyNoInteractions(creditApplicationService);
    }

    @Test
    void create_customerNotFound_returns404() throws Exception {
        when(creditApplicationService.create(any()))
                .thenThrow(new CustomerNotFoundException(CUSTOMER_ID));

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestFixtureLoader.load("credit_application/create_credit_application_request.json")))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_existingApplication_returns200() throws Exception {
        when(creditApplicationService.findById(APPLICATION_ID))
                .thenReturn(TestFixtures.creditApplicationResponseSubmitted());

        mockMvc.perform(get(ENDPOINT + "/{id}", APPLICATION_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        TestFixtureLoader.load("credit_application/credit_application_response_submitted.json")));
    }

    @Test
    void findById_unknownId_returns404() throws Exception {
        var unknownId = UUID.randomUUID();
        when(creditApplicationService.findById(unknownId))
                .thenThrow(new CreditApplicationNotFoundException(unknownId));

        mockMvc.perform(get(ENDPOINT + "/{id}", unknownId))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_noFilter_returns200PagedResponse() throws Exception {
        var page = new PageImpl<>(List.of(TestFixtures.creditApplicationResponseSubmitted()));
        when(creditApplicationService.findAll(eq(null), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(APPLICATION_ID.toString()));
    }

    @Test
    void findAll_withStatusFilter_returns200FilteredResults() throws Exception {
        var page = new PageImpl<>(List.of(TestFixtures.creditApplicationResponseSubmitted()));
        when(creditApplicationService.findAll(eq(ApplicationStatus.SUBMITTED), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get(ENDPOINT).param("status", "SUBMITTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("SUBMITTED"));
    }

    @Test
    void findByCustomer_existingCustomer_returnsPagedResponse() throws Exception {
        var page = new PageImpl<>(List.of(TestFixtures.creditApplicationResponseSubmitted()));
        when(creditApplicationService.findByCustomerId(eq(CUSTOMER_ID), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get(ENDPOINT + "/customers/{customerId}", CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].customerId").value(CUSTOMER_ID.toString()));
    }

    @Test
    void updateStatus_validTransition_returns200() throws Exception {
        when(creditApplicationService.update(eq(APPLICATION_ID), any()))
                .thenReturn(TestFixtures.creditApplicationResponseUnderReview());

        mockMvc.perform(patch(ENDPOINT + "/{id}/status", APPLICATION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestFixtureLoader.load(
                                "credit_application/update_application_status_request_under_review.json")))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        TestFixtureLoader.load("credit_application/credit_application_response_under_review.json")));
    }

    @Test
    void updateStatus_missingStatus_returns400() throws Exception {
        mockMvc.perform(patch(ENDPOINT + "/{id}/status", APPLICATION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestFixtureLoader.load(
                                "credit_application/update_application_status_request_missing_status.json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.status").exists());

        verifyNoInteractions(creditApplicationService);
    }

    @Test
    void updateStatus_invalidTransition_returns409() throws Exception {
        when(creditApplicationService.update(eq(APPLICATION_ID), any()))
                .thenThrow(new InvalidApplicationStateException(
                        "Cannot transition from APPROVED to UNDER_REVIEW"));

        mockMvc.perform(patch(ENDPOINT + "/{id}/status", APPLICATION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestFixtureLoader.load(
                                "credit_application/update_application_status_request_under_review.json")))
                .andExpect(status().isConflict());
    }

    @Test
    void delete_existingApplication_returns204() throws Exception {
        doNothing().when(creditApplicationService).delete(APPLICATION_ID);

        mockMvc.perform(delete(ENDPOINT + "/{id}", APPLICATION_ID))
                .andExpect(status().isNoContent());

        verify(creditApplicationService).delete(APPLICATION_ID);
    }

    @Test
    void delete_unknownId_returns404() throws Exception {
        var unknownId = UUID.randomUUID();
        doThrow(new CreditApplicationNotFoundException(unknownId))
                .when(creditApplicationService).delete(unknownId);

        mockMvc.perform(delete(ENDPOINT + "/{id}", unknownId))
                .andExpect(status().isNotFound());
    }

    private Customer customerWithAnnualIncome(final String annualIncome) {
        final Customer customer = new Customer();
        final EmploymentDetails employmentDetails = new EmploymentDetails();
        employmentDetails.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        employmentDetails.setEmployerName("Acme Corp");
        employmentDetails.setAnnualIncome(new BigDecimal(annualIncome));
        customer.setEmploymentDetails(employmentDetails);
        return customer;
    }
}
