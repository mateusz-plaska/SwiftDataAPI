package org.parser.swiftdata.infrastructure.error;

import org.springframework.http.HttpStatus;

public record ErrorWrapper(String errorMessage, HttpStatus expectedStatus, String uri, HttpStatus occurredStatus) {}
