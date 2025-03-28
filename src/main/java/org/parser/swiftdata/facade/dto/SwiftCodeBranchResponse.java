package org.parser.swiftdata.facade.dto;

import lombok.Getter;
import org.parser.swiftdata.infrastructure.data.SwiftCode;

@Getter
public class SwiftCodeBranchResponse {
    private final String address;
    private final String bankName;
    private final String countryISO2;
    private final String countryName;
    private final boolean isHeadquarter;
    private final String swiftCode;

    public SwiftCodeBranchResponse(SwiftCode swiftCode) {
        this.address = swiftCode.getAddress();
        this.bankName = swiftCode.getBankName();
        this.countryISO2 = swiftCode.getCountryISO2();
        this.countryName = swiftCode.getCountryName();
        this.isHeadquarter = swiftCode.isHeadquarter();
        this.swiftCode = swiftCode.getSwiftCode();
    }
}
