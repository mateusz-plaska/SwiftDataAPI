package org.parser.swiftdata.infrastructure.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "swift_codes")
@Getter
@Setter
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
    private String countryIso2;

    @Column(name = "country_name", nullable = false)
    private String countryName;

    @Column(name = "is_headquarter", nullable = false)
    private boolean isHeadquarter;

    @Column(name = "headquarter_code", length = 8)
    private String headquarterCode;
}
