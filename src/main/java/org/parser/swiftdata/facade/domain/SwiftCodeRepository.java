package org.parser.swiftdata.facade.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SwiftCodeRepository extends JpaRepository<SwiftCode, String> {

    List<SwiftCode> findByHeadquarterCode(String headquarterCode);

    List<SwiftCode> findByCountryISO2(String countryISO2);

    boolean existsByCountryName(String countryName);
}
