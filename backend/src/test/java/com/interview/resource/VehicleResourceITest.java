package com.interview.resource;

import com.interview.config.TestContainerBase;
import com.interview.domain.Vehicle;
import com.interview.domain.VehicleType;
import com.interview.dto.UpsertVehicleDto;
import com.interview.dto.VehicleDto;
import com.interview.dto.search.Direction;
import com.interview.dto.search.FieldName;
import com.interview.dto.search.PageRequestDto;
import com.interview.dto.search.PageResponseDto;
import com.interview.dto.search.SortBy;
import com.interview.dto.search.VehicleSearchCriteriaDto;
import com.interview.dto.search.VehicleSearchDto;
import com.interview.repository.VehicleRepository;
import com.interview.utils.KeycloakTokenHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Year;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VehicleResourceITest extends TestContainerBase {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private VehicleRepository vehicleRepository;

    private Vehicle testVehicle1;
    private Vehicle testVehicle2;
    private Vehicle testVehicle3;

    @BeforeEach
    public void setUp() {
        vehicleRepository.deleteAll();

        testVehicle1 = Vehicle.builder()
                .type(VehicleType.SEDAN)
                .productionYear(Year.of(2020))
                .vin("1HGBH41JXMN109186")
                .model("Corolla")
                .make("Toyota")
                .build();

        testVehicle2 = Vehicle.builder()
                .type(VehicleType.SUV)
                .productionYear(Year.of(2021))
                .vin("1HGBH41JXMN109187")
                .model("CR-V")
                .make("Honda")
                .build();

        testVehicle3 = Vehicle.builder()
                .type(VehicleType.PICKUP)
                .productionYear(Year.of(2019))
                .vin("1HGBH41JXMN109188")
                .model("F-150")
                .make("Ford")
                .build();

        testVehicle1 = vehicleRepository.save(testVehicle1);
        testVehicle2 = vehicleRepository.save(testVehicle2);
        testVehicle3 = vehicleRepository.save(testVehicle3);
    }

    @Nested
    class GetVehicleByIdTests {
        @Test
        public void getById_NoAuthenticationToken_401() {

            // When
            webTestClient.get()
                    .uri("/api/vehicles/{id}", testVehicle1.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    // Then
                    .expectStatus().isUnauthorized();
        }

        @Test
        public void getById_ExistingVehicle_Vehicle() {
            String accessToken = KeycloakTokenHelper.getUserAccessToken(keycloakContainer().getAuthServerUrl());

            // When
            webTestClient.get()
                    .uri("/api/vehicles/{id}", testVehicle1.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .exchange()
                    // Then
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(VehicleDto.class)
                    .consumeWith(response -> {
                        final VehicleDto vehicleDto = response.getResponseBody();

                        assertThat(vehicleDto.id()).isEqualTo(testVehicle1.getId());
                        assertThat(vehicleDto.make()).isEqualTo(testVehicle1.getMake());
                        assertThat(vehicleDto.model()).isEqualTo(testVehicle1.getModel());
                        assertThat(vehicleDto.vin()).isEqualTo(testVehicle1.getVin());
                        assertThat(vehicleDto.type()).isEqualTo(testVehicle1.getType());
                        assertThat(vehicleDto.productionYear()).isEqualTo(testVehicle1.getProductionYear());
                        assertThat(vehicleDto.createdDate()).isNotNull();
                        assertThat(vehicleDto.lastModifiedDate()).isNotNull();
                    });
        }

        @Test
        public void getById_NonExistentVehicle_NotFound() {
            // Given
            String accessToken = KeycloakTokenHelper.getUserAccessToken(keycloakContainer().getAuthServerUrl());

            // When
            webTestClient.get()
                    .uri("/api/vehicles/{id}", 99999L)
                    .header("Authorization", "Bearer " + accessToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    // Then
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    class CreateVehicleTests {
        @Test
        public void createVehicle_ValidData_CreatesVehicle() {
            // Given
            String accessToken = KeycloakTokenHelper.getUserAccessToken(keycloakContainer().getAuthServerUrl());

            UpsertVehicleDto createRequest = UpsertVehicleDto.builder()
                    .type(VehicleType.COUPE)
                    .productionYear("2022")
                    .vin("1HGBH41JXMN109189")
                    .model("Mustang")
                    .make("Ford")
                    .build();

            // When
            webTestClient.post()
                    .uri("/api/vehicles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .bodyValue(createRequest)
                    .exchange()
                    // Then
                    .expectStatus().isCreated()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(VehicleDto.class)
                    .consumeWith(response -> {
                        final VehicleDto vehicleDto = response.getResponseBody();

                        assertThat(vehicleDto.id()).isNotNull();
                        assertThat(vehicleDto.make()).isEqualTo(createRequest.make());
                        assertThat(vehicleDto.model()).isEqualTo(createRequest.model());
                        assertThat(vehicleDto.vin()).isEqualTo(createRequest.vin());
                        assertThat(vehicleDto.type()).isEqualTo(createRequest.type());
                        assertThat(vehicleDto.productionYear()).isEqualTo(Year.parse(createRequest.productionYear()));
                        assertThat(vehicleDto.createdBy()).isEqualTo(KeycloakTokenHelper.getTestUsername());
                        assertThat(vehicleDto.lastModifiedBy()).isEqualTo(KeycloakTokenHelper.getTestUsername());

                        assertThat(vehicleRepository.count()).isEqualTo(4);
                    });
        }

        @Test
        public void createVehicle_DuplicateVin_Conflict() {
            // Given
            String accessToken = KeycloakTokenHelper.getUserAccessToken(keycloakContainer().getAuthServerUrl());

            UpsertVehicleDto createRequest = UpsertVehicleDto.builder()
                    .type(VehicleType.SEDAN)
                    .productionYear("2021")
                    .vin(testVehicle1.getVin())
                    .model("Camry")
                    .make("Toyota")
                    .build();

            // When
            webTestClient.post()
                    .uri("/api/vehicles")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createRequest)
                    .exchange()
                    // Then
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        public void createVehicle_InvalidVin_BadRequest() {
            // Given
            String accessToken = KeycloakTokenHelper.getUserAccessToken(keycloakContainer().getAuthServerUrl());

            UpsertVehicleDto createRequest = UpsertVehicleDto.builder()
                    .type(VehicleType.SEDAN)
                    .productionYear("2021")
                    .vin("INVALID_VIN")
                    .model("Camry")
                    .make("Toyota")
                    .build();

            // When
            webTestClient.post()
                    .uri("/api/vehicles")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createRequest)
                    .exchange()
                    // Then
                    .expectStatus().isBadRequest();
        }

        @Test
        public void createVehicle_MissingRequiredFields_BadRequest() {
            // Given
            String accessToken = KeycloakTokenHelper.getUserAccessToken(keycloakContainer().getAuthServerUrl());

            UpsertVehicleDto createRequest = UpsertVehicleDto.builder()
                    .type(VehicleType.SEDAN)
                    .productionYear("2021")
                    .build();

            // When
            webTestClient.post()
                    .uri("/api/vehicles")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createRequest)
                    .exchange()
                    // Then
                    .expectStatus().isBadRequest();
        }
    }

    @Nested
    class UpdateVehicleTests {
        @Test
        public void updateVehicle_ExistingVehicle_UpdatesVehicle() {
            // Given
            String accessToken = KeycloakTokenHelper.getUserAccessToken(keycloakContainer().getAuthServerUrl());

            UpsertVehicleDto updateRequest = UpsertVehicleDto.builder()
                    .type(VehicleType.HATCHBACK)
                    .productionYear("2023")
                    .vin("1HGBH41JXMN109190")
                    .model("Civic")
                    .make("Honda")
                    .build();

            // When
            webTestClient.put()
                    .uri("/api/vehicles/{id}", testVehicle1.getId())
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateRequest)
                    .exchange()
                    // Then
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(VehicleDto.class)
                    .consumeWith(response -> {
                        final VehicleDto vehicleDto = response.getResponseBody();

                        assertThat(vehicleDto.id()).isEqualTo(testVehicle1.getId());
                        assertThat(vehicleDto.make()).isEqualTo(updateRequest.make());
                        assertThat(vehicleDto.model()).isEqualTo(updateRequest.model());
                        assertThat(vehicleDto.vin()).isEqualTo(updateRequest.vin());
                        assertThat(vehicleDto.type()).isEqualTo(updateRequest.type());
                        assertThat(vehicleDto.productionYear()).isEqualTo(Year.parse(updateRequest.productionYear()));
                    });
        }

        @Test
        public void updateVehicle_SameVin_UpdatesVehicle() {
            // Given
            String accessToken = KeycloakTokenHelper.getUserAccessToken(keycloakContainer().getAuthServerUrl());

            UpsertVehicleDto updateRequest = UpsertVehicleDto.builder()
                    .type(VehicleType.HATCHBACK)
                    .productionYear("2023")
                    .vin(testVehicle2.getVin())
                    .model("Civic")
                    .make("Honda")
                    .build();

            // When
            webTestClient.put()
                    .uri("/api/vehicles/{id}", testVehicle2.getId())
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateRequest)
                    .exchange()
                    // Then
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(VehicleDto.class)
                    .consumeWith(response -> {
                        final VehicleDto vehicleDto = response.getResponseBody();

                        assertThat(vehicleDto.id()).isEqualTo(testVehicle2.getId());
                        assertThat(vehicleDto.make()).isEqualTo(updateRequest.make());
                        assertThat(vehicleDto.model()).isEqualTo(updateRequest.model());
                        assertThat(vehicleDto.vin()).isEqualTo(updateRequest.vin());
                        assertThat(vehicleDto.type()).isEqualTo(updateRequest.type());
                        assertThat(vehicleDto.productionYear()).isEqualTo(Year.parse(updateRequest.productionYear()));
                    });
        }

        @Test
        public void updateVehicle_NonExistentVehicle_NotFound() {
            // Given
            String accessToken = KeycloakTokenHelper.getUserAccessToken(keycloakContainer().getAuthServerUrl());

            UpsertVehicleDto updateRequest = UpsertVehicleDto.builder()
                    .type(VehicleType.SEDAN)
                    .productionYear("2021")
                    .vin("1HGBH41JXMN109190")
                    .model("Camry")
                    .make("Toyota")
                    .build();

            // When
            webTestClient.put()
                    .uri("/api/vehicles/{id}", 99999L)
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateRequest)
                    .exchange()
                    // Then
                    .expectStatus().isNotFound();
        }

        @Test
        public void updateVehicle_DuplicateVin_Conflict() {
            // Given
            String accessToken = KeycloakTokenHelper.getUserAccessToken(keycloakContainer().getAuthServerUrl());

            UpsertVehicleDto updateRequest = UpsertVehicleDto.builder()
                    .type(VehicleType.SEDAN)
                    .productionYear("2021")
                    .vin(testVehicle2.getVin())
                    .model("Camry")
                    .make("Toyota")
                    .build();

            // When
            webTestClient.put()
                    .uri("/api/vehicles/{id}", testVehicle1.getId())
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateRequest)
                    .exchange()
                    // Then
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT);
        }
    }

    @Nested
    class DeleteVehicleTests {
        @Test
        public void deleteVehicle_ExistingVehicle_DeletesVehicle() {
            // Given
            String accessToken = KeycloakTokenHelper.getUserAccessToken(keycloakContainer().getAuthServerUrl());

            // When

            webTestClient.delete()
                    .uri("/api/vehicles/{id}", testVehicle1.getId())
                    .header("Authorization", "Bearer " + accessToken)
                    .exchange()
                    // Then
                    .expectStatus().isNoContent();

            assertThat(vehicleRepository.count()).isEqualTo(2);
            assertThat(vehicleRepository.findById(testVehicle1.getId())).isEmpty();
        }

        @Test
        public void deleteVehicle_NonExistentVehicle_NoContent() {
            // Given
            String accessToken = KeycloakTokenHelper.getUserAccessToken(keycloakContainer().getAuthServerUrl());

            // When
            webTestClient.delete()
                    .uri("/api/vehicles/{id}", 99999L)
                    .header("Authorization", "Bearer " + accessToken)
                    .exchange()
                    // Then
                    .expectStatus().isNoContent();

            assertThat(vehicleRepository.count()).isEqualTo(3);
        }
    }


    @Nested
    class SearchVehiclesTests {
        @Test
        public void searchVehicles_FilterByVehicleType_FilteredVehicles() {
            // Given
            String accessToken = KeycloakTokenHelper.getUserAccessToken(keycloakContainer().getAuthServerUrl());

            VehicleSearchDto searchRequest = VehicleSearchDto.builder()
                    .searchCriteriaDto(VehicleSearchCriteriaDto.builder()
                            .includingVehicleTypes(Set.of(VehicleType.SUV))
                            .build())
                    .pageRequestDto(new PageRequestDto(0, 10, null))
                    .build();

            // When
            webTestClient.post()
                    .uri("/api/vehicles/search")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(searchRequest)
                    .exchange()
                    // Then
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(new ParameterizedTypeReference<PageResponseDto<VehicleDto>>() {
                    })
                    .consumeWith(response -> {
                        final PageResponseDto<VehicleDto> pageResponseDto = response.getResponseBody();

                        assertThat(pageResponseDto.totalElements()).isEqualTo(1);
                        assertThat(pageResponseDto.content()).hasSize(1);

                        assertThat(pageResponseDto.content().get(0).id()).isEqualTo(testVehicle2.getId());
                    });
        }

        @Test
        public void searchVehicles_FilterByProductionYear_FilteredVehicles() {
            // Given
            String accessToken = KeycloakTokenHelper.getUserAccessToken(keycloakContainer().getAuthServerUrl());

            VehicleSearchDto searchRequest = VehicleSearchDto.builder()
                    .searchCriteriaDto(VehicleSearchCriteriaDto.builder()
                            .productionYearFrom("2020")
                            .productionYearTo("2021")
                            .build())
                    .pageRequestDto(new PageRequestDto(0, 10, null))
                    .build();

            // When
            webTestClient.post()
                    .uri("/api/vehicles/search")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(searchRequest)
                    .exchange()
                    // Then
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(new ParameterizedTypeReference<PageResponseDto<VehicleDto>>() {
                    })
                    .consumeWith(response -> {
                        final PageResponseDto<VehicleDto> pageResponseDto = response.getResponseBody();

                        assertThat(pageResponseDto.totalElements()).isEqualTo(1);
                        assertThat(pageResponseDto.content()).hasSize(1);

                        assertThat(pageResponseDto.content().get(0).id()).isEqualTo(testVehicle1.getId());
                    });
        }

        @Test
        public void searchVehicles_WithoutSearchCriteria_BadRequest() {
            // Given
            String accessToken = KeycloakTokenHelper.getUserAccessToken(keycloakContainer().getAuthServerUrl());

            VehicleSearchDto searchRequest = VehicleSearchDto.builder()
                    .searchCriteriaDto(null)
                    .pageRequestDto(new PageRequestDto(0, 2, null))
                    .build();

            // When
            webTestClient.post()
                    .uri("/api/vehicles/search")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(searchRequest)
                    .exchange()
                    // Then
                    .expectStatus().isBadRequest();
        }

        @Test
        public void searchVehicles_WithSorting_SortedResults() {
            // Given
            String accessToken = KeycloakTokenHelper.getUserAccessToken(keycloakContainer().getAuthServerUrl());

            List<SortBy> sortBy = List.of(new SortBy(FieldName.PRODUCTION_YEAR, Direction.DESC));
            VehicleSearchDto searchRequest = VehicleSearchDto.builder()
                    .searchCriteriaDto(VehicleSearchCriteriaDto.builder()
                            .productionYearFrom("2019").build())
                    .pageRequestDto(new PageRequestDto(0, 10, sortBy))
                    .build();

            // When
            webTestClient.post()
                    .uri("/api/vehicles/search")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(searchRequest)
                    .exchange()
                    // Then
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(new ParameterizedTypeReference<PageResponseDto<VehicleDto>>() {
                    })
                    .consumeWith(response -> {
                        final PageResponseDto<VehicleDto> pageResponseDto = response.getResponseBody();

                        assertThat(pageResponseDto.totalElements()).isEqualTo(3);
                        assertThat(pageResponseDto.content()).hasSize(3);

                        assertThat(pageResponseDto.content().get(0).id()).isEqualTo(testVehicle2.getId());
                        assertThat(pageResponseDto.content().get(1).id()).isEqualTo(testVehicle1.getId());
                        assertThat(pageResponseDto.content().get(2).id()).isEqualTo(testVehicle3.getId());
                    });
        }

        @Test
        public void searchVehicles_FilterByVin_SpecificVehicle() {
            // Given
            String accessToken = KeycloakTokenHelper.getUserAccessToken(keycloakContainer().getAuthServerUrl());

            VehicleSearchDto searchRequest = VehicleSearchDto.builder()
                    .searchCriteriaDto(VehicleSearchCriteriaDto.builder()
                            .includingVins(Set.of(testVehicle3.getVin()))
                            .build())
                    .pageRequestDto(new PageRequestDto(0, 10, null))
                    .build();

            // When
            webTestClient.post()
                    .uri("/api/vehicles/search")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(searchRequest)
                    .exchange()
                    // Then
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(new ParameterizedTypeReference<PageResponseDto<VehicleDto>>() {
                    })
                    .consumeWith(response -> {
                        final PageResponseDto<VehicleDto> pageResponseDto = response.getResponseBody();

                        assertThat(pageResponseDto.totalElements()).isEqualTo(1);
                        assertThat(pageResponseDto.content()).hasSize(1);

                        assertThat(pageResponseDto.content().get(0).id()).isEqualTo(testVehicle3.getId());
                    });
        }

        @Test
        public void searchVehicles_ExcludeVehicleTypes_FilteredVehicles() {
            // Given
            String accessToken = KeycloakTokenHelper.getUserAccessToken(keycloakContainer().getAuthServerUrl());

            VehicleSearchDto searchRequest = VehicleSearchDto.builder()
                    .searchCriteriaDto(VehicleSearchCriteriaDto.builder()
                            .excludingVehicleTypes(Set.of(VehicleType.SEDAN, VehicleType.SUV))
                            .build())
                    .pageRequestDto(new PageRequestDto(0, 10, null))
                    .build();

            // When
            webTestClient.post()
                    .uri("/api/vehicles/search")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(searchRequest)
                    .exchange()
                    // Then
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(new ParameterizedTypeReference<PageResponseDto<VehicleDto>>() {
                    })
                    .consumeWith(response -> {
                        final PageResponseDto<VehicleDto> pageResponseDto = response.getResponseBody();

                        assertThat(pageResponseDto.totalElements()).isEqualTo(1);
                        assertThat(pageResponseDto.content()).hasSize(1);

                        assertThat(pageResponseDto.content().get(0).id()).isEqualTo(testVehicle3.getId());
                    });
        }

        @Test
        public void searchVehicles_InvalidPageRequest_BadRequest() {
            // Given
            String accessToken = KeycloakTokenHelper.getUserAccessToken(keycloakContainer().getAuthServerUrl());

            VehicleSearchDto searchRequest = VehicleSearchDto.builder()
                    .searchCriteriaDto(VehicleSearchCriteriaDto.builder().build())
                    .pageRequestDto(new PageRequestDto(-1, 0, null))
                    .build();

            // When
            webTestClient.post()
                    .uri("/api/vehicles/search")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(searchRequest)
                    .exchange()
                    // Then
                    .expectStatus().isBadRequest();
        }
    }

}
