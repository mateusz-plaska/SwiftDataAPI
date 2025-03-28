package org.parser.swiftdata.infrastructure.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SwiftCodeRepository extends JpaRepository<SwiftCode, String> {}
