package org.parser.swiftdata.facade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SwiftCodeRequest {

    @NotBlank(message = "SWIFT code cannot be empty")
    @Size(min = 11, max = 11, message = "SWIFT code must have exactly 11 characters")
    private String swiftCode;

    @NotBlank(message = "Bank name cannot be empty")
    private String bankName;

    @NotBlank(message = "Country ISO2 code cannot be empty")
    @Size(min = 2, max = 2, message = "Country ISO2 code must have exactly 2 characters")
    private String countryISO2;

    @NotBlank(message = "Country name cannot be empty")
    private String countryName;

    private String address;

    private Boolean isHeadquarter;
}
