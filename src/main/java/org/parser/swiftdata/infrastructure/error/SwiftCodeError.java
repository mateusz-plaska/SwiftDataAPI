package org.parser.swiftdata.infrastructure.error;

public sealed interface SwiftCodeError extends Error {

    record SwiftCodeNotFoundById(String id) implements SwiftCodeError {}
}
