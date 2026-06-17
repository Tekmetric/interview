package com.interview.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.CustomerRequest;
import com.interview.dto.ServicePackageRequest;
import com.interview.dto.StatusUpdateRequest;
import com.interview.dto.SubscriptionRequest;
import com.interview.entity.Customer;
import com.interview.repository.CustomerRepository;
import com.interview.repository.ServicePackageRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full‑stack integration tests for {@link ServicePackageController}.
 *
 * <p>The Spring context is started on a random port and the real database (H2 in‑memory for the <code>test</code>
 * profile) is used. Security filters are disabled via {@code addFilters = false} because the purpose of these
 * tests is to validate controller–service interactions and global exception handling, not JWT flow.</p>
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("ServicePackageController ‑ Integration")
public class ServicePackageControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ServicePackageRepository servicePackageRepository;
    @Autowired
    private CustomerRepository customerRepository;

    private ServicePackageRequest validRequest;

    @BeforeEach
    void setUp() {
        servicePackageRepository.deleteAll();
        validRequest = new ServicePackageRequest(
            "Premium Wash",
            "Exterior + interior + wax",
            new BigDecimal("29.99")
        );
    }

    @Nested
    @DisplayName("POST /api/v1/service-packages")
    class CreateServicePackage {
        @Test
        @DisplayName("should create service package and return 201")
        void shouldCreatePackage() throws Exception {
            mockMvc.perform(post("/api/v1/service-packages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(validRequest.name()))
                .andExpect(jsonPath("$.monthlyPrice").value(is(29.99)));
        }

        @Test
        @DisplayName("should fail validation and return 400 when name is blank")
        void shouldFailValidation() throws Exception {
            ServicePackageRequest bad = new ServicePackageRequest(" ", "desc", new BigDecimal("9.99"));

            mockMvc.perform(post("/api/v1/service-packages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
        }

        @Test
        @DisplayName("should return 400 when package name already exists")
        void shouldReturn400OnDuplicateName() throws Exception {
            // create first package
            mockMvc.perform(post("/api/v1/service-packages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated());

            // attempt duplicate
            mockMvc.perform(post("/api/v1/service-packages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already exists")));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/service-packages/{id}")
    class GetServicePackageById {
        @Test
        @DisplayName("should return service package when found")
        void shouldReturnPackage() throws Exception {
            MvcResult result = mockMvc.perform(post("/api/v1/service-packages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andReturn();

            JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
            long id = body.path("id").asLong();

            mockMvc.perform(get("/api/v1/service-packages/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(validRequest.name()));
        }

        @Test
        @DisplayName("should return 404 when not found")
        void shouldReturn404() throws Exception {
            mockMvc.perform(get("/api/v1/service-packages/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("SERVICE_PACKAGE_NOT_FOUND"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/service-packages/{id}")
    class UpdateServicePackage {
        private long id;

        @BeforeEach
        void init() throws Exception {
            // create baseline package and capture its ID
            MvcResult result = mockMvc.perform(post("/api/v1/service-packages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andReturn();
            id = objectMapper.readTree(result.getResponse().getContentAsString()).path("id").asLong();
        }

        @Test
        @DisplayName("should update name and price and return 200")
        void shouldUpdatePackage() throws Exception {
            ServicePackageRequest updateReq = new ServicePackageRequest(
                "Ultimate Wash",           // name changed
                "All‑inclusive detailing", // description changed
                new BigDecimal("49.99")    // price changed
            );

            mockMvc.perform(put("/api/v1/service-packages/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ultimate Wash"))
                .andExpect(jsonPath("$.monthlyPrice").value(is(49.99)));
        }

        @Test
        @DisplayName("should return 400 when duplicate name")
        void shouldReturn400OnDuplicateName() throws Exception {
            // create another distinct package so we can trigger duplicate
            ServicePackageRequest other = new ServicePackageRequest(
                "Basic Wash", "Exterior only", new BigDecimal("9.99"));
            mockMvc.perform(post("/api/v1/service-packages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(other)))
                .andExpect(status().isCreated());

            // attempt to rename first to duplicate name
            ServicePackageRequest dupNameReq = new ServicePackageRequest(
                "Basic Wash", "Updated desc", new BigDecimal("19.99"));

            mockMvc.perform(put("/api/v1/service-packages/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dupNameReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already exists")));
        }

        @Test
        @DisplayName("should return 404 when updating non‑existent ID")
        void shouldReturn404WhenNotFound() throws Exception {
            ServicePackageRequest updateReq = new ServicePackageRequest(
                "Ultimate Wash", "All‑inclusive", new BigDecimal("49.99"));

            mockMvc.perform(put("/api/v1/service-packages/{id}", 9999)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("SERVICE_PACKAGE_NOT_FOUND"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/service-packages")
    class GetAllServicePackages {
        @Test
        @DisplayName("should return empty list when no packages exist")
        void shouldReturnEmptyList() throws Exception {
            mockMvc.perform(get("/api/v1/service-packages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("should return list with packages when present")
        void shouldReturnList() throws Exception {
            // create two packages via controller to stay at same level
            mockMvc.perform(post("/api/v1/service-packages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated());

            ServicePackageRequest second = new ServicePackageRequest(
                "Basic Wash", "Exterior only", new BigDecimal("9.99"));
            mockMvc.perform(post("/api/v1/service-packages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(second)))
                .andExpect(status().isCreated());

            mockMvc.perform(get("/api/v1/service-packages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @DisplayName("should filter by active=true param")
        void shouldFilterActiveTrue() throws Exception {
            // create one active package (default active=true)
            mockMvc.perform(post("/api/v1/service-packages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated());

            mockMvc.perform(get("/api/v1/service-packages")
                    .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/service-packages/paginated")
    class GetServicePackagesPaginated {
        @Test
        @DisplayName("should return paginated result with metadata")
        void shouldReturnPaginated() throws Exception {
            // seed 2 packages
            mockMvc.perform(post("/api/v1/service-packages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated());

            ServicePackageRequest second = new ServicePackageRequest(
                "Basic Wash", "Exterior only", new BigDecimal("9.99"));
            mockMvc.perform(post("/api/v1/service-packages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(second)))
                .andExpect(status().isCreated());

            mockMvc.perform(get("/api/v1/service-packages/paginated")
                    .param("page", "0")
                    .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.size").value(1));
        }

        @Test
        @DisplayName("should filter active=true in paginated list")
        void shouldFilterActiveTrue() throws Exception {
            // create active package
            mockMvc.perform(post("/api/v1/service-packages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated());

            mockMvc.perform(get("/api/v1/service-packages/paginated")
                    .param("active", "true")
                    .param("page", "0")
                    .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value(validRequest.name()));
        }

        @Test
        @DisplayName("should return empty page when no packages exist")
        void shouldReturnEmptyPage() throws Exception {
            mockMvc.perform(get("/api/v1/service-packages/paginated")
                    .param("page", "0")
                    .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.page.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/service-packages/{id}/status")
    class UpdateServicePackageStatus {
        private long id;

        @BeforeEach
        void initPackage() throws Exception {
            MvcResult result = mockMvc.perform(post("/api/v1/service-packages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated()).andReturn();
            id = objectMapper.readTree(result.getResponse().getContentAsString()).path("id").asLong();
        }

        @Test
        @DisplayName("should deactivate package and return 200")
        void shouldDeactivate() throws Exception {
            StatusUpdateRequest deactivate = new StatusUpdateRequest(false);

            mockMvc.perform(patch("/api/v1/service-packages/{id}/status", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(deactivate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
        }

        @Test
        @DisplayName("should reactivate inactive package and return 200")
        void shouldReactivate() throws Exception {
            // deactivate first
            mockMvc.perform(patch("/api/v1/service-packages/{id}/status", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new StatusUpdateRequest(false))))
                .andExpect(status().isOk());

            // now activate
            mockMvc.perform(patch("/api/v1/service-packages/{id}/status", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new StatusUpdateRequest(true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
        }

        @Test
        @DisplayName("should return 404 when package not found")
        void shouldReturn404() throws Exception {
            mockMvc.perform(patch("/api/v1/service-packages/{id}/status", 9999)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new StatusUpdateRequest(false))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("SERVICE_PACKAGE_NOT_FOUND"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/service-packages/{id}/subscribe")
    class SubscribeCustomerToPackage {
        private long packageId;
        private long customerId;

        @BeforeEach
        void initData() throws Exception {
            // create service package
            MvcResult pkgResult = mockMvc.perform(post("/api/v1/service-packages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated()).andReturn();
            packageId = objectMapper.readTree(pkgResult.getResponse().getContentAsString()).path("id").asLong();

            // create customer via controller so validation is applied, using unique email to avoid PK clash
            String uniqueEmail = "john.doe+" + System.nanoTime() + "@example.com";
            CustomerRequest custReq = new CustomerRequest("John", null, "Doe", uniqueEmail, "555-0101", null, null, null);
            MvcResult custRes = mockMvc.perform(post("/api/v1/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(custReq)))
                .andExpect(status().isCreated()).andReturn();
            customerId = objectMapper.readTree(custRes.getResponse().getContentAsString()).path("id").asLong();
        }

        @Test
        @DisplayName("should subscribe customer and return 204")
        void shouldSubscribeCustomer() throws Exception {
            SubscriptionRequest subReq = new SubscriptionRequest(customerId);

            mockMvc.perform(post("/api/v1/service-packages/{id}/subscribe", packageId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(subReq)))
                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("should return 400 when customer already subscribed")
        void shouldReturn400OnDuplicateSubscription() throws Exception {
            SubscriptionRequest subReq = new SubscriptionRequest(customerId);
            // first subscribe
            mockMvc.perform(post("/api/v1/service-packages/{id}/subscribe", packageId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(subReq)))
                .andExpect(status().isNoContent());
            // duplicate subscribe
            mockMvc.perform(post("/api/v1/service-packages/{id}/subscribe", packageId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(subReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already subscribed")));
        }

        @Test
        @DisplayName("should return 404 when service package not found")
        void shouldReturn404OnMissingPackage() throws Exception {
            SubscriptionRequest subReq = new SubscriptionRequest(customerId);
            mockMvc.perform(post("/api/v1/service-packages/{id}/subscribe", 9999)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(subReq)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("SERVICE_PACKAGE_NOT_FOUND"));
        }

        @Test
        @DisplayName("should return 404 when customer not found")
        void shouldReturn404OnMissingCustomer() throws Exception {
            SubscriptionRequest subReq = new SubscriptionRequest(9999L);
            mockMvc.perform(post("/api/v1/service-packages/{id}/subscribe", packageId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(subReq)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("CUSTOMER_NOT_FOUND"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/service-packages/{id}/customers/{customerId}")
    class UnsubscribeCustomerFromPackage {

        @Test
        @DisplayName("should unsubscribe customer and return 204")
        void shouldUnsubscribeCustomer() throws Exception {
            // create service package
            MvcResult res = mockMvc.perform(post("/api/v1/service-packages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andReturn();
            long pkgId = objectMapper.readTree(res.getResponse().getContentAsString()).path("id").asLong();

            // create customer
            Customer c = new Customer();
            c.setFirstName("John");
            c.setLastName("Doe");
            c.setEmail("john." + System.nanoTime() + "@example.com");
            long customerId = customerRepository.save(c).getId();

            // subscribe first
            SubscriptionRequest subReq = new SubscriptionRequest(customerId);
            mockMvc.perform(post("/api/v1/service-packages/{id}/subscribe", pkgId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(subReq)))
                .andExpect(status().isNoContent());

            // unsubscribe
            mockMvc.perform(delete("/api/v1/service-packages/{id}/customers/{customerId}", pkgId, customerId))
                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("should return 400 when customer not subscribed")
        void shouldReturn400WhenNotSubscribed() throws Exception {
            // create package
            MvcResult res = mockMvc.perform(post("/api/v1/service-packages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andReturn();
            long pkgId = objectMapper.readTree(res.getResponse().getContentAsString()).path("id").asLong();

            // create customer (never subscribe)
            Customer c = new Customer();
            c.setFirstName("Jane");
            c.setLastName("Smith");
            c.setEmail("jane." + System.nanoTime() + "@example.com");
            long customerId = customerRepository.save(c).getId();

            mockMvc.perform(delete("/api/v1/service-packages/{id}/customers/{customerId}", pkgId, customerId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("not subscribed")));
        }

        @Test
        @DisplayName("should return 404 when service package not found")
        void shouldReturn404WhenPackageNotFound() throws Exception {
            // create customer
            Customer c = new Customer();
            c.setFirstName("Mike");
            c.setLastName("Brown");
            c.setEmail("mike." + System.nanoTime() + "@example.com");
            long customerId = customerRepository.save(c).getId();

            mockMvc.perform(delete("/api/v1/service-packages/{id}/customers/{customerId}", 9999, customerId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("SERVICE_PACKAGE_NOT_FOUND"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/service-packages/{id}/subscribers")
    class GetServicePackageSubscribers {

        @Test
        @DisplayName("should return subscribers list when present")
        void shouldReturnSubscribers() throws Exception {
            // create package
            MvcResult res = mockMvc.perform(post("/api/v1/service-packages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andReturn();
            long pkgId = objectMapper.readTree(res.getResponse().getContentAsString()).path("id").asLong();

            // create & subscribe two customers
            for (int i = 0; i < 2; i++) {
                Customer c = new Customer();
                c.setFirstName("Cust" + i);
                c.setLastName("L" + i);
                c.setEmail("cust" + i + "." + System.nanoTime() + "@example.com");
                long custId = customerRepository.save(c).getId();
                SubscriptionRequest subReq = new SubscriptionRequest(custId);
                mockMvc.perform(post("/api/v1/service-packages/{id}/subscribe", pkgId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subReq)))
                    .andExpect(status().isNoContent());
            }

            mockMvc.perform(get("/api/v1/service-packages/{id}/subscribers", pkgId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(2));
        }

        @Test
        @DisplayName("should return empty list when no subscribers")
        void shouldReturnEmptyList() throws Exception {
            // create package without subscribers
            MvcResult res = mockMvc.perform(post("/api/v1/service-packages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andReturn();
            long pkgId = objectMapper.readTree(res.getResponse().getContentAsString()).path("id").asLong();

            mockMvc.perform(get("/api/v1/service-packages/{id}/subscribers", pkgId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(0));
        }

        @Test
        @DisplayName("should return 404 when service package not found")
        void shouldReturn404WhenPackageNotFound() throws Exception {
            mockMvc.perform(get("/api/v1/service-packages/{id}/subscribers", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("SERVICE_PACKAGE_NOT_FOUND"));
        }
    }
}
