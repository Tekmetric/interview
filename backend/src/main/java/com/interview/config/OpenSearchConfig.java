package com.interview.config;

import org.apache.http.HttpHost;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class OpenSearchConfig {

    @Value("${opensearch.uris:http://localhost:9200}")
    private String opensearchUri;

    @Bean
    public RestHighLevelClient customOpenSearchClient() {

        return new RestHighLevelClient(
                RestClient.builder(
                        HttpHost.create(opensearchUri)
                )
        );
    }
}