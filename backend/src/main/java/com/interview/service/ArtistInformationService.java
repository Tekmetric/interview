package com.interview.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.config.CacheConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.util.UriEncoder;

@Service
public class ArtistInformationService {
    public static final Logger LOG = LoggerFactory.getLogger(ArtistInformationService.class);
    public static final String JSON_PATH_TO_ARTIST_INFO = "/artists/0/disambiguation";

    private final RestTemplate restTemplate;

    @Value("${application.artist-information-url}")
    private String artistInformationApiURL;

    public ArtistInformationService() {
        this.restTemplate = new RestTemplate();
    }

    @Cacheable(CacheConfiguration.CACHE_ARTIST_INFO)
    public String getArtistInformation(String artistName) {
        LOG.info("Getting info on {}", artistName);

        ResponseEntity<String> response = restTemplate.getForEntity(artistInformationApiURL + UriEncoder.encode(artistName), String.class);
        var mapper = new ObjectMapper();
        JsonNode root;

        try {
            root = mapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            return StringUtils.EMPTY;
        }

        JsonNode name = root.at(JSON_PATH_TO_ARTIST_INFO);
        return name.toPrettyString();
    }

}
