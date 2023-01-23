package com.interview.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.interview.dto.shop.CreateShopDto;
import com.interview.dto.shop.ShopDto;
import com.interview.dto.user.AuthResponseDto;
import com.interview.dto.user.LoginRequestDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShopControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String jwtToken;

    @Before
    public void setUp() {
        LoginRequestDto loginDto = new LoginRequestDto("test@gmail.com", "tekmetric");
        ResponseEntity<AuthResponseDto> response = restTemplate.postForEntity("/api/auth/login", loginDto, AuthResponseDto.class);
        jwtToken = response.getBody().getAccessToken();
    }

    @Test
    public void createShop_shouldReturnCreatedStatus() {
        CreateShopDto createShopDto = new CreateShopDto();
        createShopDto.setTitle("Test Shop");
        createShopDto.setAvgOrder(0);
        createShopDto.setImageFilename("image.png");
        createShopDto.setLocation("Texas");
        createShopDto.setStaffNumber(12);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);
        HttpEntity<CreateShopDto> request = new HttpEntity<>(createShopDto, headers);

        ResponseEntity<ShopDto> response = restTemplate.postForEntity("/api/shops", request, ShopDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Test Shop");
    }

    @Test
    public void listShops_shouldReturnOkStatus() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);
        HttpEntity request = new HttpEntity(headers);

        String search = "Auto";
        int page = 0;
        String url = String.format("/api/shops?search=%s&page=%d", search, page);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new SimpleModule().addDeserializer(Page.class, new PageDeserializer()));
        restTemplate.getRestTemplate().setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter(objectMapper)));

        ParameterizedTypeReference<Page<ShopDto>> typeRef = new ParameterizedTypeReference<Page<ShopDto>>() {};
        ResponseEntity<Page<ShopDto>> response = restTemplate.exchange(url, HttpMethod.GET, request, typeRef);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isNotEmpty();
    }

    class PageDeserializer extends JsonDeserializer<Page<ShopDto>> {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public Page<ShopDto> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            List<ShopDto> shopDtos = objectMapper.readValue(node.get("content").toString(), new TypeReference<List<ShopDto>>(){});
            return new PageImpl<>(shopDtos, PageRequest.of(node.get("number").asInt(), node.get("size").asInt()), node.get("totalElements").asLong());
        }
    }
}