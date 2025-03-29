package org.parser.swiftdata.infrastructure.error;

public sealed interface SwiftCodeError extends Error {

    record SwiftCodeNotFoundById(String id) implements SwiftCodeError {}

    record CountryNotFoundByCountryISO2(String countryISO2code) implements SwiftCodeError {}

    record SwiftCodeIdExists(String id) implements SwiftCodeError {}
}
