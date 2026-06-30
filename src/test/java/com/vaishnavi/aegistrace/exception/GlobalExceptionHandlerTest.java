package com.vaishnavi.aegistrace.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

class GlobalExceptionHandlerTest {

    @Test
    void unhandledExceptionsDoNotExposeInternalMessagesToApiClients() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest("GET", "/api/events"));

        ResponseEntity<ApiError> response = handler.handleAllExceptions(
                new IllegalStateException("database password leaked in internal trace"),
                request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).doesNotContain("password");
        assertThat(response.getBody().getMessage()).contains("unexpected error");
    }

    @Test
    void missingStaticResourcesReturnNotFound() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest("GET", "/missing-route"));

        ResponseEntity<ApiError> response = handler.handleMissingResource(
                new NoResourceFoundException(HttpMethod.GET, "/missing-route"),
                request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("not found");
    }
}
