package com.interview.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.interview.model.Event;
import com.interview.model.EventDocument;
import com.interview.repository.EventRepository;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.delete.DeleteRequest;
import org.opensearch.action.delete.DeleteResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EventSearchService {

    private final RestHighLevelClient client;
    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    public EventSearchService(RestHighLevelClient client, EventRepository eventRepository) {
        this.client = client;
        this.eventRepository = eventRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public List<EventDocument> search(
            String keyword,
            String city,
            int page,
            int size
    ) throws Exception {

        SearchRequest request = new SearchRequest("events");
        SearchSourceBuilder builder = new SearchSourceBuilder();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (keyword != null && !keyword.isBlank()) {
            boolQuery.should(QueryBuilders.matchQuery("name", keyword));
            boolQuery.should(QueryBuilders.matchQuery("description", keyword));
        }

        if (city != null && !city.isBlank()) {
            boolQuery.filter(QueryBuilders.matchQuery("location", city));
        }

        builder.query(boolQuery);
        builder.sort("eventDate", SortOrder.ASC);
        builder.from((page - 1) * size);
        builder.size(size);

        request.source(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        List<EventDocument> results = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            EventDocument doc = objectMapper.readValue(hit.getSourceAsString(), EventDocument.class);
            results.add(doc);
        }
        return results;
    }

    public void indexAllEvents() throws Exception {
        int page = 0;
        int size = 200;  // batch size
        Page<Event> eventPage;

        do {
            eventPage = eventRepository.findAll(PageRequest.of(page, size));
            BulkRequest bulk = new BulkRequest();

            for (Event event : eventPage) {
                EventDocument doc = EventDocument.fromEvent(event);
                bulk.add(new IndexRequest("events")
                        .id(doc.getId())
                        .source(objectMapper.writeValueAsString(doc), XContentType.JSON));
            }

            if (bulk.numberOfActions() > 0) {
                BulkResponse response = client.bulk(bulk, RequestOptions.DEFAULT);
                if (response.hasFailures()) {
                    System.out.println("Some failures occurred: " + response.buildFailureMessage());
                } else {
                    System.out.println("Indexed batch " + page + ", count = " + bulk.numberOfActions());
                }
            }

            page++; // next batch

        } while (!eventPage.isLast());
    }

    /**
     * Index a single event to OpenSearch
     */
    public void indexEvent(Event event) throws Exception {
        EventDocument doc = EventDocument.fromEvent(event);
        IndexRequest request = new IndexRequest("events")
                .id(doc.getId())
                .source(objectMapper.writeValueAsString(doc), XContentType.JSON);

        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println("Indexed event: " + event.getId() + " with result: " + response.getResult());
    }

    /**
     * Delete an event from OpenSearch by ID
     */
    public void deleteEvent(UUID eventId) throws Exception {
        DeleteRequest request = new DeleteRequest("events", eventId.toString());
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        System.out.println("Deleted event: " + eventId + " with result: " + response.getResult());
    }
}
