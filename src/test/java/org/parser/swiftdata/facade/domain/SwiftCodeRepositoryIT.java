package org.parser.swiftdata.facade.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class SwiftCodeRepositoryIT {

    @Autowired
    private SwiftCodeRepository repository;

    @MockBean
    private CommandLineRunner loadSwiftData;

    private SwiftCode headquarter;
    private SwiftCode branch;

    @BeforeEach
    void setUp() {
        headquarter = new SwiftCode("TEST1234XXX", "Bank HQ", "HQ Address", "PL", "Poland", true, "TEST1234");
        branch = new SwiftCode("TEST1234001", "Bank Branch", "Branch Address", "PL", "Poland", false, "TEST1234");

        repository.saveAndFlush(headquarter);
        repository.saveAndFlush(branch);
    }

    @Test
    void shouldFindSwiftCodeById() {
        // given
        // when
        Optional<SwiftCode> found = repository.findById(headquarter.getSwiftCode());
        // then
        assertThat(found).isPresent();
        assertThat(found.get().getBankName()).isEqualTo(headquarter.getBankName());
    }

    @Test
    void shouldReturnEmptyIfSwiftCodeNotFound() {
        // given
        // when
        Optional<SwiftCode> found = repository.findById("NONEXISTENT");
        // then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindBranchesByHeadquarterCode() {
        // given
        // when
        List<SwiftCode> branches = repository.findByHeadquarterCode(headquarter.getHeadquarterCode());
        // then
        assertThat(branches).hasSize(2);
        assertTrue(branches.stream().anyMatch(b -> b.getSwiftCode().equals(headquarter.getSwiftCode())));
        assertTrue(branches.stream().anyMatch(b -> b.getSwiftCode().equals(branch.getSwiftCode())));
    }

    @Test
    void shouldReturnEmptyListIfNoBranchesFound() {
        // given
        // when
        List<SwiftCode> branches = repository.findByHeadquarterCode("UNKNOWN1");
        // then
        assertThat(branches).isEmpty();
    }

    @Test
    void shouldFindSwiftCodesByCountryISO2() {
        // given
        // when
        List<SwiftCode> swiftCodes = repository.findByCountryISO2(headquarter.getCountryISO2());
        // then
        assertThat(swiftCodes).hasSize(2);
        System.out.println(swiftCodes);
        assertTrue(swiftCodes.stream().anyMatch(s -> s.getSwiftCode().equals(headquarter.getSwiftCode())));
        assertTrue(swiftCodes.stream().anyMatch(s -> s.getSwiftCode().equals(branch.getSwiftCode())));
    }

    @Test
    void shouldReturnEmptyListIfNoSwiftCodesForCountry() {
        // given
        // when
        List<SwiftCode> swiftCodes = repository.findByCountryISO2("XX");
        // then
        assertThat(swiftCodes).isEmpty();
    }

    @Test
    void shouldCheckIfCountryExistsByName() {
        // given
        // when
        boolean exists = repository.existsByCountryName(headquarter.getCountryName());
        // then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseIfCountryDoesNotExist() {
        // given
        // when
        boolean exists = repository.existsByCountryName("Never land");
        // then
        assertThat(exists).isFalse();
    }

    @Test
    void shouldDeleteSwiftCodeById() {
        // given
        // when
        repository.deleteById(branch.getSwiftCode());
        // then
        assertFalse(repository.existsById(branch.getSwiftCode()));
    }

    @Test
    void shouldSaveSwiftCodeAndFindItById() {
        // given
        SwiftCode swiftCode = new SwiftCode("ADDED123XXX", "Bank HQ", "HQ Address", "PL", "Poland", true, "ADDED123");

        // when
        repository.saveAndFlush(swiftCode);

        // then
        Optional<SwiftCode> found = repository.findById(swiftCode.getSwiftCode());
        assertThat(found).isPresent();
        assertThat(found.get().getSwiftCode()).isEqualTo(swiftCode.getSwiftCode());
        assertThat(found.get().getBankName()).isEqualTo(swiftCode.getBankName());
    }

    @Test
    void shouldSaveMultipleSwiftCodesAndRetrieveThem() {
        // given
        SwiftCode swiftCode1 = new SwiftCode("CODE1111XXX", "Bank A", "Address A", "US", "USA", true, "CODE1111");
        SwiftCode swiftCode2 = new SwiftCode("CODE2222XXX", "Bank B", "Address B", "US", "USA", true, "CODE2222");

        // when
        repository.saveAllAndFlush(List.of(swiftCode1, swiftCode2));

        // then
        List<SwiftCode> swiftCodes = repository.findAll();
        assertThat(swiftCodes).hasSize(4);
        assertTrue(swiftCodes.stream().anyMatch(s -> s.getSwiftCode().equals(headquarter.getSwiftCode())));
        assertTrue(swiftCodes.stream().anyMatch(s -> s.getSwiftCode().equals(branch.getSwiftCode())));
        assertTrue(swiftCodes.stream().anyMatch(s -> s.getSwiftCode().equals(swiftCode1.getSwiftCode())));
        assertTrue(swiftCodes.stream().anyMatch(s -> s.getSwiftCode().equals(swiftCode2.getSwiftCode())));
    }

    @Test
    void shouldNotAllowNullValuesInRequiredFields() {
        // given
        SwiftCode swiftCode = SwiftCode.builder()
                .swiftCode("NULL1234567")
                .bankName(null)
                .address("Some Address")
                .countryISO2("ES")
                .countryName("Spain")
                .isHeadquarter(false)
                .headquarterCode("NULL1234")
                .build();

        // when / then
        assertThatThrownBy(() -> repository.saveAndFlush(swiftCode))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
