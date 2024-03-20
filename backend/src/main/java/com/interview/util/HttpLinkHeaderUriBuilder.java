package com.interview.util;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class HttpLinkHeaderUriBuilder {

    private static final String GET_SHOPS_PAGINATED_URL = "<http://localhost:8080/api/shops/page?number=%s&size=%s>; ";

    public HttpHeaders buildHttpLinkHeaderForPage(Page<?> page) {
        var links = new ArrayList<String>();
        if (page.hasPrevious()) {
            links.add(String.format(GET_SHOPS_PAGINATED_URL, page.getNumber() - 1, page.getSize()) + "rel=\"prev\"");
        }
        if (page.hasNext()) {
            links.add(String.format(GET_SHOPS_PAGINATED_URL, page.getNumber() + 1, page.getSize()) + "rel=\"next\"");
        }
        var httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.LINK, String.join(",", links));
        return httpHeaders;
    }
}
