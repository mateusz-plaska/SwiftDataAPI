package org.parser.swiftdata.facade;

import org.parser.swiftdata.facade.dto.CountrySwiftCodesResponse;
import org.parser.swiftdata.facade.dto.SwiftCodeBranchResponse;
import org.parser.swiftdata.facade.dto.SwiftCodeRequest;
import org.parser.swiftdata.infrastructure.error.Result;

public interface SwiftCodeService {

    Result<SwiftCodeBranchResponse> getSwiftCodeById(String swiftCodeId);

    Result<CountrySwiftCodesResponse> getSwiftCodesByCountry(String countryISO2);

    Result<String> addSwiftCode(SwiftCodeRequest swiftCodeRequest);

    Result<String> deleteSwiftCode(String swiftCodeId);
}
