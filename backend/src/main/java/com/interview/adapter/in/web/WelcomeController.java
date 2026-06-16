package com.interview.adapter.in.web;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.interview.api.WelcomeApi;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController implements WelcomeApi {

    /*
     * I wasn't sure if you wanted me to keep the /api/welcome endpoint or not. I added it to the OpenAPI spec (http://localhost:8080/swagger-ui.html)
     * with a separate tag so now it lives in its own controller class. The only difference is that now it returns a
     * ResponseEntity<String> instead of just a regular String.
     */

    @Override
    public ResponseEntity<String> index() {
        return ResponseEntity
                .ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body("Welcome to the interview project!");
    }
}
