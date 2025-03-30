package org.parser.swiftdata.infrastructure.validator;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.parser.swiftdata.facade.dto.SwiftCodeRequest;
import org.parser.swiftdata.facade.domain.SwiftCode;
import org.parser.swiftdata.facade.domain.SwiftCodeRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("swiftCodeValidator")
@RequiredArgsConstructor
public class SwiftCodeValidator implements Validator {

    private final SwiftCodeRepository repository;

    @Override
    public boolean supports(Class<?> clazz) {
        return SwiftCode.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SwiftCodeRequest swiftCodeRequest = (SwiftCodeRequest) target;

        validateCountryData(swiftCodeRequest.getCountryISO2(), swiftCodeRequest.getCountryName(), errors);
        validateHeadquarterData(swiftCodeRequest.getIsHeadquarter(), swiftCodeRequest.getSwiftCode(), errors);
    }

    private void validateCountryData(String countryISO2, String countryName, Errors errors) {
        List<SwiftCode> recordsForCountry = repository.findByCountryISO2(countryISO2);

        if (recordsForCountry.isEmpty()) {
            if (repository.existsByCountryName(countryName)) {
                errors.rejectValue(
                        "countryISO2",
                        "countryName.conflict",
                        "Provided country " + "name exists while the countryISO2 does not");
            }
        } else {
            String existingCountryName = recordsForCountry.getFirst().getCountryName();
            if (!existingCountryName.equals(countryName)) {
                errors.rejectValue(
                        "countryName",
                        "countryName.mismatch",
                        "Provided country " + "name does not match to the existing one for the countryISO2");
            }
        }
    }

    private void validateHeadquarterData(Boolean isHeadquarter, String swiftCodeId, Errors errors) {
        if (isHeadquarter != null && swiftCodeId.endsWith("XXX") != isHeadquarter) {
            errors.rejectValue(
                    "isHeadquarter",
                    "isHeadquarter.invalid",
                    "Swift code data " + "does not match the provided isHeadquarter value");
        }
    }
}
