package com.interview.framework;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.application.configuration.jakson.View;
import com.interview.application.rest.v1.common.dto.AbstractAuditingDto;
import com.interview.domain.service.common.InternationalizationService;
import com.interview.framework.config.TestConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ContextConfiguration(classes = TestConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ExtendWith(SpringExtension.class)
@Slf4j
public abstract class BaseUtilResourceIntegrationTest {

    @Autowired
    public WebApplicationContext webApplicationContext;
    @Qualifier("objectMapperBuilder")
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected InternationalizationService internationalizationService;
    @Autowired
    protected MockMvc restMvc;

    /**
     * Define the resource api url
     * @return the resource api url.
     */
    protected abstract String getBaseAPIUrl();

    /**
     * Create a new entity using a REST api create operation
     *
     * @param dto create entity dto
     * @return result ResultActions
     */
    public ResultActions createEntity(final AbstractAuditingDto<?> dto) throws Exception {
        return createEntity(dto, getBaseAPIUrl());
    }

    /**
     * Create a new entity using a REST api create operation
     *
     * @param dto create entity dto
     * @param baseAPIUrl the url
     * @return result ResultActions
     */
    public ResultActions createEntity(
            final AbstractAuditingDto<?> dto,
            final String baseAPIUrl) throws Exception {
        return restMvc.perform(post(baseAPIUrl)
                .contentType(APPLICATION_JSON)
                .content(dto != null ? convertObjectToJsonBytes(dto) : new byte[1]));
    }

    /**
     * Get entity using the REST API and returns the ResultActions
     */
    public ResultActions getEntity(final Object entityId) throws Exception {
        String requestPath = getBaseAPIUrl() + "/" + (entityId == null ? "" : entityId);
        return restMvc.perform(get(requestPath)
                .contentType(APPLICATION_JSON));
    }

    /**
     * Update entity using the REST API and returns the ResultActions
     */
    public ResultActions updateEntity(
            final Object entityId,
            final AbstractAuditingDto<?> dto) throws Exception {
        String requestPath = getBaseAPIUrl() + "/" + (entityId == null ? "" : entityId);
        return restMvc.perform(put(requestPath)
                .contentType(APPLICATION_JSON)
                .content(dto != null ? convertObjectToJsonBytes(dto) : new byte[1]));
    }

    /**
     * Delete entity using the REST API and returns the ResultActions
     */
    public ResultActions deleteEntity(final Object entityId) throws Exception {
        String requestPath = getBaseAPIUrl() + "/" + (entityId == null ? "" : entityId);
        return restMvc.perform(delete(requestPath)
                .contentType(APPLICATION_JSON));
    }

    /**
     * Get entity using the REST API and returns the ResultActions
     *
     * @return - ResultActions
     */
    public ResultActions getAllEntities() throws Exception {
        return restMvc.perform(get(getBaseAPIUrl() + "?page=0")
                .contentType(APPLICATION_JSON));
    }

    /**
     * Convert an object to JSON byte array.
     *
     * @param object the object to convert
     * @return the JSON byte array or null if a {@link JsonProcessingException} is found
     */
    protected final byte[] convertObjectToJsonBytes(final Object object) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.writerWithView(View.Internal.class).writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            log.error("Cannot convertObjectToJsonBytes due: " + e.getMessage());
            return null;
        }
    }

    /**
     * Converts a JSON byte array to an {@link Object}
     *
     * @param bytes The JSON Serialized information
     * @param clazz The expected {@link Class} of the serialized object
     * @return an {@link Object} ( of type {@link T}, inferred by context) that is deserialized from the given {@code bytes}
     * or {@code null} if any {@link Exception} is thrown
     */
    protected final <T> T readAnyObjectFromJsonBytes(final byte[] bytes, Class<?> clazz) {
        if (bytes == null) {
            return null;
        }
        try {
            return objectMapper.readerWithView(View.Internal.class).forType(clazz).readValue(bytes);
        } catch (Exception e) {
            log.error("Cannot readObjectFromJsonBytes due: " + e.getMessage());
            return null;
        }
    }

    /**
     * Converts a JSON byte array to a list of {@link Object}s
     *
     * @param bytes The JSON Serialized form of the list
     * @param clazz The expected {@link Class} of the serialized objects in the {@link List}
     * @return an {@link List} of {@link T}s (inferred by context) that is deserialized from the given {@code bytes}
     * or {@code null} if any {@link Exception} is thrown
     */
    protected final <T> List<T> readListOfObjectFromJsonBytes(final byte[] bytes, Class<? extends T> clazz) {
        if (bytes == null) {
            return null;
        }
        try {
            return objectMapper.readValue(bytes, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            log.error("Cannot readObjectFromJsonBytes due: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
