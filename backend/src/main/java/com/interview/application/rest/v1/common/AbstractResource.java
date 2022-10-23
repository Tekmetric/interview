/**
 * The base spring boot resource class.
 */
package com.interview.application.rest.v1.common;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
public abstract class AbstractResource {
}
