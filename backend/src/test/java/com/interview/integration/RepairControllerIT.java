package com.interview.integration;

import com.interview.dto.repairorder.RepairOrderDto;
import com.interview.integration.dto.PagedResponse;
import com.interview.model.RepairOrderStatus;
import com.interview.repository.RepairOrderRepository;
import com.interview.repository.model.RepairOrderEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RepairControllerIT {

    private static final String BASE_URL = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RepairOrderRepository repairOrderRepository;

    @AfterEach
    void tearDown() {
        repairOrderRepository.deleteAll();
    }

    @Test
    void givenValidCreateRepairOrderRequest_whenCreateRepair_thenShouldCreateRepairOrder() {
        var reqEnt = RequestEntity.post(BASE_URL + port + "/api/v1/repair-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "vin": "WAUZZZ8V3JA123456",
                          "carModel": "Audi A3",
                          "issueDescription": "Car doesn't start"
                        }
                        """);

        var response = restTemplate.exchange(reqEnt, RepairOrderDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.vin()).isEqualTo("WAUZZZ8V3JA123456");
        assertThat(body.carModel()).isEqualTo("Audi A3");
        assertThat(body.issueDescription()).isEqualTo("Car doesn't start");
        assertThat(body.status()).isEqualTo(RepairOrderStatus.DRAFT);
    }

    @Test
    void givenRepairOrderAlreadyPersisted_whenGetById_ShouldReturnPersistedEntity() {
        RepairOrderEntity entity = repairOrderRepository.save(RepairOrderEntity.builder()
                .vin("WAUZZZ8V3JA123456")
                .carModel("carModel")
                .status(RepairOrderStatus.DRAFT)
                .issueDescription("Car broken")
                .build());


        var reqEntity = RequestEntity.get(BASE_URL + port + "/api/v1/repair-orders/" + entity.getId())
                .accept(MediaType.APPLICATION_JSON).build();
        var response = restTemplate.exchange(reqEntity, RepairOrderDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var repairOrderDto = response.getBody();

        assertThat(repairOrderDto).isNotNull();
        assertThat(repairOrderDto.vin()).isEqualTo(entity.getVin());
        assertThat(repairOrderDto.status()).isEqualTo(entity.getStatus());
    }

    @Test
    void givenRepairOrdersAlreadyPersisted_whenAllPaginated_ShouldReturnPersistedEntitiesPaginated() {
        for (int i = 0; i < 3; i++) {
            repairOrderRepository.save(RepairOrderEntity.builder()
                    .vin("vin" + i)
                    .carModel("carModel" + i)
                    .status(RepairOrderStatus.DRAFT)
                    .issueDescription("Car broken" + i)
                    .build());
        }

        var reqFirstPage = RequestEntity.get(BASE_URL + port + "/api/v1/repair-orders?page=0&size=2&sort=id")
                .accept(MediaType.APPLICATION_JSON).build();
        var responsePage1 = restTemplate.exchange(reqFirstPage, new ParameterizedTypeReference<PagedResponse<RepairOrderDto>>() {
        });
        assertThat(responsePage1.getStatusCode()).isEqualTo(HttpStatus.OK);
        var firstPageBody = responsePage1.getBody();

        assertThat(firstPageBody).isNotNull();
        assertThat(firstPageBody.content()).hasSize(2);

        var reqSecondPage = RequestEntity.get(BASE_URL + port + "/api/v1/repair-orders?page=1&size=2&sort=id")
                .accept(MediaType.APPLICATION_JSON).build();
        var responsePage2 = restTemplate.exchange(reqSecondPage, new ParameterizedTypeReference<PagedResponse<RepairOrderDto>>() {
        });
        assertThat(responsePage2.getStatusCode()).isEqualTo(HttpStatus.OK);
        var secondPageBody = responsePage2.getBody();

        assertThat(secondPageBody).isNotNull();
        assertThat(secondPageBody.content()).hasSize(1);
    }

    @Test
    void givenRepairOrderAlreadyPersisted_whenUpdateRepairOrder_ShouldUpdatePersistedEntity() {
        RepairOrderEntity entity = repairOrderRepository.save(RepairOrderEntity.builder()
                .vin("WAUZZZ8V3JA123456")
                .carModel("carModel")
                .status(RepairOrderStatus.DRAFT)
                .issueDescription("Car broken")
                .build());
        var reqEntity = RequestEntity.put(BASE_URL + port + "/api/v1/repair-orders/" + entity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "issueDescription": "Another issue",
                            "status": "IN_PROGRESS"
                        }
                        """);


        var response = restTemplate.exchange(reqEntity, RepairOrderDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var repairOrderDto = response.getBody();

        assertThat(repairOrderDto).isNotNull();
        assertThat(repairOrderDto.vin()).isEqualTo(entity.getVin());
        assertThat(repairOrderDto.status()).isEqualTo(RepairOrderStatus.IN_PROGRESS);
        assertThat(repairOrderDto.issueDescription()).isEqualTo("Another issue");

        var optionalUpdatedEntity = repairOrderRepository.findById(entity.getId());
        assertThat(optionalUpdatedEntity).isPresent();
        RepairOrderEntity repairOrder = optionalUpdatedEntity.get();
        assertThat(repairOrder.getCreatedAt()).isBefore(repairOrder.getUpdatedAt());
        assertThat(repairOrder.getIssueDescription()).isEqualTo("Another issue");
        assertThat(repairOrder.getStatus()).isEqualTo(RepairOrderStatus.IN_PROGRESS);
    }

    @Test
    void givenRepairOrderAlreadyPersisted_whenDeleteById_ShouldDeleteEntity() {
        RepairOrderEntity entity = repairOrderRepository.save(RepairOrderEntity.builder()
                .vin("WAUZZZ8V3JA123456")
                .carModel("carModel")
                .status(RepairOrderStatus.DRAFT)
                .issueDescription("Car broken")
                .build());


        var reqEntity = RequestEntity.delete(BASE_URL + port + "/api/v1/repair-orders/" + entity.getId()).build();
        var response = restTemplate.exchange(reqEntity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(repairOrderRepository.findById(entity.getId())).isEmpty();
    }
}
