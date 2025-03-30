package org.parser.swiftdata.facade.dto;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.parser.swiftdata.facade.domain.SwiftCode;

@Getter
public class CountrySwiftCodesResponse {
    private final String countryISO2;
    private final String countryName;
    private final List<SwiftCodeBranchResponse> swiftCodes;

    public CountrySwiftCodesResponse(String countryISO2, String countryName, List<SwiftCode> swiftCodes) {
        this.countryISO2 = countryISO2;
        this.countryName = countryName;
        this.swiftCodes = swiftCodes.stream().map(SwiftCodeBranchResponse::new).collect(Collectors.toList());
    }
}
