package org.parser.swiftdata.facade.domain;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.parser.swiftdata.facade.SwiftCodeService;
import org.parser.swiftdata.facade.dto.CountrySwiftCodesResponse;
import org.parser.swiftdata.facade.dto.SwiftCodeBranchResponse;
import org.parser.swiftdata.facade.dto.SwiftCodeHeadquarterResponse;
import org.parser.swiftdata.facade.dto.SwiftCodeRequest;
import org.parser.swiftdata.infrastructure.data.SwiftCode;
import org.parser.swiftdata.infrastructure.data.SwiftCodeRepository;
import org.parser.swiftdata.infrastructure.error.Result;
import org.parser.swiftdata.infrastructure.error.SwiftCodeError;
import org.springframework.stereotype.Service;

// delete
// tests
// docker
// warunek w 1 endpoint
// zwracanie message
// autowired in controller

@Service
@Slf4j
record SwiftCodeServiceImpl(SwiftCodeRepository repository) implements SwiftCodeService {

    // jesli centralna nie jest oddziałem to w headquarter_code centrali mamy puste pule a tu pobieramy
    // 8 pierwszych znakow z ID
    @Override
    public Result<SwiftCodeBranchResponse> getSwiftCodeById(String swiftCodeId) {
        SwiftCode swiftCode = repository.findById(swiftCodeId).orElse(null);

        if (swiftCode == null) {
            return Result.failure(new SwiftCodeError.SwiftCodeNotFoundById(swiftCodeId));
        }

        if (swiftCode.isHeadquarter()) {
            List<SwiftCode> branches = repository.findByHeadquarterCode(swiftCode.getHeadquarterCode());
            return Result.success(new SwiftCodeHeadquarterResponse(swiftCode, branches));
        }

        return Result.success(new SwiftCodeBranchResponse(swiftCode));
    }

    @Override
    public Result<CountrySwiftCodesResponse> getSwiftCodesByCountry(String countryISO2) {
        List<SwiftCode> swiftCodes = repository.findByCountryISO2(countryISO2);

        if (swiftCodes.isEmpty()) {
            return Result.failure(new SwiftCodeError.CountryNotFoundByCountryISO2(countryISO2));
        }

        String countryName = swiftCodes.getFirst().getCountryName();
        return Result.success(new CountrySwiftCodesResponse(countryISO2, countryName, swiftCodes));
    }

    @Override
    public Result<String> addSwiftCode(SwiftCodeRequest swiftCodeRequest) {
        if (repository.existsById(swiftCodeRequest.getSwiftCode())) {
            return Result.failure(new SwiftCodeError.SwiftCodeIdExists(swiftCodeRequest.getSwiftCode()));
        }

        if (swiftCodeRequest.getIsHeadquarter() == null) {
            swiftCodeRequest.setIsHeadquarter(swiftCodeRequest.getSwiftCode().endsWith("XXX"));
        }

        SwiftCode createdSwiftCode = new SwiftCode(
                swiftCodeRequest.getSwiftCode(),
                swiftCodeRequest.getBankName(),
                swiftCodeRequest.getAddress(),
                swiftCodeRequest.getCountryISO2(),
                swiftCodeRequest.getCountryName(),
                swiftCodeRequest.getIsHeadquarter(),
                swiftCodeRequest.getSwiftCode().substring(0, 8));

        repository.save(createdSwiftCode);

        return Result.success("message: swift code created successfully");
    }

    @Override
    public Result<String> deleteSwiftCode(String swiftCodeId) {
        return null;
    }
}
