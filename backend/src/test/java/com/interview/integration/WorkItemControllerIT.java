package com.interview.integration;

import com.interview.dto.login.LoginResponse;
import com.interview.dto.workitem.WorkItemDto;
import com.interview.integration.dto.PagedResponse;
import com.interview.model.RepairOrderStatus;
import com.interview.repository.RepairOrderRepository;
import com.interview.repository.WorkItemRepository;
import com.interview.repository.model.RepairOrderEntity;
import com.interview.repository.model.WorkItemEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WorkItemControllerIT {

    private static final String BASE_URL = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WorkItemRepository workItemRepository;

    @Autowired
    private RepairOrderRepository repairOrderRepository;

    @AfterEach
    void cleanDb() {
        workItemRepository.deleteAll();
        repairOrderRepository.deleteAll();
    }

    private RepairOrderEntity createRepairOrder() {
        return repairOrderRepository.save(
                RepairOrderEntity.builder()
                        .vin("WAUZZZ8V3JA123456")
                        .carModel("Audi A3")
                        .status(RepairOrderStatus.DRAFT)
                        .issueDescription("Some issue")
                        .build()
        );
    }

    private String itemsUrl(long roId) {
        return BASE_URL + port + "/api/v1/repair-orders/" + roId + "/items";
    }

    @Test
    void givenValidRequest_whenCreateWorkItem_thenShouldCreate() {
        var ro = createRepairOrder();

        var req = RequestEntity.post(itemsUrl(ro.getId()))
                .header("Authorization", getToken("Staff"))
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "name": "Oil Change",
                          "description": "Replace engine oil and filter",
                          "price": 99.99
                        }
                        """);

        var response = restTemplate.exchange(req, WorkItemDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var dto = response.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.name()).isEqualTo("Oil Change");
        assertThat(dto.description()).isEqualTo("Replace engine oil and filter");
        assertThat(dto.price()).isEqualTo(99.99f);

        assertThat(workItemRepository.count()).isEqualTo(1);
    }

    @Test
    void givenItemsPersisted_whenGetAll_thenReturnPaginated() {
        var ro = createRepairOrder();

        workItemRepository.saveAll(
                List.of(
                        WorkItemEntity.builder()
                                .name("Work A").description("Desc A").price(10).repairOrderEntity(ro).build(),
                        WorkItemEntity.builder()
                                .name("Work B").description("Desc B").price(20).repairOrderEntity(ro).build(),
                        WorkItemEntity.builder()
                                .name("Work C").description("Desc C").price(30).repairOrderEntity(ro).build()
                )
        );

        var req1 = RequestEntity.get(itemsUrl(ro.getId()) + "?page=0&size=2&sort=id")
                .header("Authorization", getToken("Staff"))
                .accept(MediaType.APPLICATION_JSON)
                .build();

        var resp1 = restTemplate.exchange(req1,
                new ParameterizedTypeReference<PagedResponse<WorkItemDto>>() {
                });

        assertThat(resp1.getStatusCode()).isEqualTo(HttpStatus.OK);

        var page1 = resp1.getBody();
        assertThat(page1).isNotNull();
        assertThat(page1.content()).hasSize(2);

        var req2 = RequestEntity.get(itemsUrl(ro.getId()) + "?page=1&size=2&sort=id")
                .header("Authorization", getToken("Staff"))
                .accept(MediaType.APPLICATION_JSON)
                .build();

        var resp2 = restTemplate.exchange(req2,
                new ParameterizedTypeReference<PagedResponse<WorkItemDto>>() {
                });

        assertThat(resp2.getStatusCode()).isEqualTo(HttpStatus.OK);

        var page2 = resp2.getBody();
        assertThat(page2).isNotNull();
        assertThat(page2.content()).hasSize(1);
    }

    @Test
    void givenItemPersisted_whenUpdate_thenShouldUpdate() {
        var ro = createRepairOrder();

        var entity = workItemRepository.save(
                WorkItemEntity.builder()
                        .name("Old Name")
                        .description("Old Desc")
                        .price(50)
                        .repairOrderEntity(ro)
                        .build()
        );

        var req = RequestEntity.put(itemsUrl(ro.getId()) + "/" + entity.getId())
                .header("Authorization", getToken("Staff"))
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "description": "Updated Description",
                          "price": 123.45
                        }
                        """);

        var response = restTemplate.exchange(req, WorkItemDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var dto = response.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.description()).isEqualTo("Updated Description");
        assertThat(dto.price()).isEqualTo(123.45f);

        var updated = workItemRepository.findById(entity.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getDescription()).isEqualTo("Updated Description");
        assertThat(updated.get().getPrice()).isEqualTo(123.45f);
    }

    @Test
    void givenItemPersisted_whenDelete_thenShouldSoftDelete() {
        var ro = createRepairOrder();

        var entity = workItemRepository.save(
                WorkItemEntity.builder()
                        .name("Remove me")
                        .description("To be deleted")
                        .price(44.0f)
                        .repairOrderEntity(ro)
                        .build()
        );

        var req = RequestEntity.delete(itemsUrl(ro.getId()) + "/" + entity.getId())
                .header("Authorization", getToken("Staff"))
                .build();
        var response = restTemplate.exchange(req, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        var softDeletedEntity = workItemRepository.findById(entity.getId());

        assertThat(softDeletedEntity).isPresent();
        assertTrue(softDeletedEntity.get().isDeleted());
    }

    private String getToken(String role) {
        var username = "John";
        var password = "Test1234!";
        if (role.equalsIgnoreCase("ADMIN")) {
            username = "Admin";
        }
        var req = RequestEntity.post(BASE_URL + port + "/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "username": "%s",
                          "password": "%s"
                        }
                        """.formatted(username, password));
        var response = restTemplate.exchange(req, LoginResponse.class);
        Assertions.assertNotNull(response.getBody());
        return "Bearer " + response.getBody().token();
    }
}
