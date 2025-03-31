package org.parser.swiftdata.facade.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "swift_codes",
        indexes = {
            @Index(name = "idx_country_iso2", columnList = "country_iso2"),
            @Index(name = "idx_headquarter_code", columnList = "headquarter_code")
        })
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SwiftCode {

    @Id
    @Column(name = "swift_code", length = 11)
    private String swiftCode;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "address")
    private String address;

    @Column(name = "country_iso2", nullable = false, length = 2)
    private String countryISO2;

    @Column(name = "country_name", nullable = false)
    private String countryName;

    @Column(name = "is_headquarter", nullable = false)
    private boolean isHeadquarter;

    @Column(name = "headquarter_code", length = 8)
    private String headquarterCode;
}
