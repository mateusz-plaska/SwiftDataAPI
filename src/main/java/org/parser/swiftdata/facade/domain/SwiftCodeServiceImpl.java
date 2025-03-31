package org.parser.swiftdata.facade.domain;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.parser.swiftdata.facade.SwiftCodeService;
import org.parser.swiftdata.facade.dto.*;
import org.parser.swiftdata.infrastructure.error.Result;
import org.parser.swiftdata.infrastructure.error.SwiftCodeError;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
class SwiftCodeServiceImpl implements SwiftCodeService {

    private final SwiftCodeRepository repository;

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
    @Transactional
    public Result<ApiResponse> addSwiftCode(SwiftCodeRequest swiftCodeRequest) {
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
                swiftCodeRequest.getCountryISO2().toUpperCase(),
                swiftCodeRequest.getCountryName().toUpperCase(),
                swiftCodeRequest.getIsHeadquarter(),
                swiftCodeRequest.getSwiftCode().substring(0, 8));

        repository.save(createdSwiftCode);
        return Result.success(new ApiResponse("swift code created successfully"));
    }

    @Override
    @Transactional
    public Result<ApiResponse> deleteSwiftCode(String swiftCodeId) {
        SwiftCode swiftCodeToDelete = repository.findById(swiftCodeId).orElse(null);
        if (swiftCodeToDelete == null) {
            return Result.failure(new SwiftCodeError.SwiftCodeNotFoundById(swiftCodeId));
        }

        List<String> toDeleteIds = List.of(swiftCodeToDelete.getSwiftCode());

        if (swiftCodeToDelete.isHeadquarter()) {
            List<SwiftCode> swiftCodesToDelete =
                    repository.findByHeadquarterCode(swiftCodeToDelete.getHeadquarterCode());
            toDeleteIds =
                    swiftCodesToDelete.stream().map(SwiftCode::getSwiftCode).toList();
        }

        repository.deleteAllById(toDeleteIds);
        return Result.success(new ApiResponse(
                String.format("swift code deleted successfully, deleted %d record(s)", toDeleteIds.size())));
    }
}
