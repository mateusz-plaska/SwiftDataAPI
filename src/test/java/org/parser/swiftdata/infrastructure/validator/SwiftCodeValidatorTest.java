package org.parser.swiftdata.infrastructure.validator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.parser.swiftdata.facade.dto.SwiftCodeRequest;
import org.parser.swiftdata.facade.domain.SwiftCode;
import org.parser.swiftdata.facade.domain.SwiftCodeRepository;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

@ExtendWith(MockitoExtension.class)
public class SwiftCodeValidatorTest {

    @InjectMocks
    private SwiftCodeValidator validator;

    @Mock
    private SwiftCodeRepository repository;

    private Errors getErrors(Object target) {
        return new BeanPropertyBindingResult(target, target.getClass().getSimpleName());
    }

    // validateCountryData()

    @Test
    void validateCountryData_passes_whenCountryDataIsConsistent() {
        // given
        SwiftCodeRequest request = SwiftCodeRequest.builder()
                .swiftCode("ABC123XXX")
                .bankName("Bank name")
                .countryISO2("PL")
                .countryName("Poland")
                .build();

        SwiftCode existing = new SwiftCode("DUMMY123XXX", "Bank", "Addr", "PL", "Poland", true, "DUMMY");
        when(repository.findByCountryISO2("PL")).thenReturn(List.of(existing));
        Errors errors = getErrors(request);

        // when
        validator.validate(request, errors);

        // then
        assertFalse(errors.hasFieldErrors("countryISO2"));
        assertFalse(errors.hasFieldErrors("countryName"));
    }

    @Test
    void validateCountryData_rejects_whenNoRecordsForCountryISOButCountryNameExists() {
        // given
        SwiftCodeRequest request = SwiftCodeRequest.builder()
                .swiftCode("ABC123XXX")
                .bankName("Bank name")
                .countryISO2("US")
                .countryName("Poland")
                .build();

        when(repository.findByCountryISO2("US")).thenReturn(Collections.emptyList());
        when(repository.existsByCountryName("Poland")).thenReturn(true);
        Errors errors = getErrors(request);

        // when
        validator.validate(request, errors);

        // then
        assertTrue(errors.hasFieldErrors("countryISO2"));
        assertFalse(errors.hasFieldErrors("countryName"));
    }

    @Test
    void validateCountryData_rejects_whenExistingCountryNameMismatch() {
        // given
        SwiftCodeRequest request = SwiftCodeRequest.builder()
                .swiftCode("ABC123XXX")
                .bankName("Bank name")
                .countryISO2("PL")
                .countryName("France")
                .build();

        SwiftCode existing = new SwiftCode("DUMMY123XXX", "Bank", "Addr", "PL", "Poland", true, "DUMMY123");
        when(repository.findByCountryISO2("PL")).thenReturn(List.of(existing));
        Errors errors = getErrors(request);

        // when
        validator.validate(request, errors);

        // then
        assertFalse(errors.hasFieldErrors("countryISO2"));
        assertTrue(errors.hasFieldErrors("countryName"));
    }

    // validateHeadquarterData()

    @Test
    void validateHeadquarterData_passes_whenConsistent() {
        // given
        SwiftCodeRequest request = SwiftCodeRequest.builder()
                .swiftCode("ABC12345XXX")
                .bankName("Bank name")
                .countryISO2("PL")
                .countryName("Poland")
                .isHeadquarter(true)
                .build();

        Errors errors = getErrors(request);

        // when
        validator.validate(request, errors);

        // then
        assertFalse(errors.hasFieldErrors("isHeadquarter"));
    }

    @Test
    void validateHeadquarterData_passes_whenIsHeadquarterIsNull() {
        // given
        SwiftCodeRequest request = SwiftCodeRequest.builder()
                .swiftCode("ABC12345XXX")
                .bankName("Bank name")
                .countryISO2("PL")
                .countryName("Poland")
                .isHeadquarter(null)
                .build();

        Errors errors = getErrors(request);

        // when
        validator.validate(request, errors);

        // then
        assertFalse(errors.hasFieldErrors("isHeadquarter"));
    }

    @Test
    void validateHeadquarterData_rejects_whenInconsistencyDetected_trueCase() {
        // given
        SwiftCodeRequest request = SwiftCodeRequest.builder()
                .swiftCode("ABC12345YYY")
                .bankName("Bank name")
                .countryISO2("PL")
                .countryName("Poland")
                .isHeadquarter(true)
                .build();

        Errors errors = getErrors(request);

        // when
        validator.validate(request, errors);

        // then
        assertTrue(errors.hasFieldErrors("isHeadquarter"));
    }

    @Test
    void validateHeadquarterData_rejects_whenInconsistencyDetected_falseCase() {
        // given
        SwiftCodeRequest request = SwiftCodeRequest.builder()
                .swiftCode("ABC12345XXX")
                .bankName("Bank name")
                .countryISO2("PL")
                .countryName("Poland")
                .isHeadquarter(false)
                .build();

        Errors errors = getErrors(request);

        // when
        validator.validate(request, errors);

        // then
        assertTrue(errors.hasFieldErrors("isHeadquarter"));
    }

    // both

    @Test
    void validate_passes_whenAllDataIsConsistent() {
        // given
        SwiftCodeRequest request = SwiftCodeRequest.builder()
                .swiftCode("ABC12345XxX")
                .bankName("Bank name")
                .countryISO2("PL")
                .countryName("Poland")
                .isHeadquarter(false)
                .build();

        SwiftCode existing = new SwiftCode("DUMMY123XXX", "Bank", "Addr", "PL", "Poland", true, "DUMMY123");
        when(repository.findByCountryISO2("PL")).thenReturn(List.of(existing));
        Errors errors = getErrors(request);

        // when
        validator.validate(request, errors);

        // then
        assertFalse(errors.hasErrors());
    }

    @Test
    void validate_reportsMultipleErrors_whenDataIsInvalid() {
        // given
        SwiftCodeRequest request = SwiftCodeRequest.builder()
                .swiftCode("ABC12345XxX")
                .bankName("Bank name")
                .countryISO2("PL")
                .countryName("France")
                .isHeadquarter(true)
                .build();

        SwiftCode existing = new SwiftCode("DUMMY123XXX", "Bank", "Addr", "PL", "Poland", true, "DUMMY123");
        when(repository.findByCountryISO2("PL")).thenReturn(List.of(existing));
        Errors errors = getErrors(request);

        // when
        validator.validate(request, errors);

        // then
        assertFalse(errors.hasFieldErrors("countryISO2"));
        assertTrue(errors.hasFieldErrors("countryName"));
        assertTrue(errors.hasFieldErrors("isHeadquarter"));
    }
}
