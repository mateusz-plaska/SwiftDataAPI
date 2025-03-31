package org.parser.swiftdata.facade.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.parser.swiftdata.facade.dto.*;
import org.parser.swiftdata.infrastructure.error.Result;
import org.parser.swiftdata.infrastructure.error.SwiftCodeError;

@ExtendWith(MockitoExtension.class)
public class SwiftCodeServiceImplTest {

    @InjectMocks
    private SwiftCodeServiceImpl swiftCodeService;

    @Mock
    private SwiftCodeRepository repository;

    private SwiftCodeRequest validRequest;

    @BeforeEach
    void setup() {
        validRequest = SwiftCodeRequest.builder()
                .swiftCode("TEST1234XXX")
                .bankName("Test Bank Name")
                .address("Test Address")
                .countryISO2("PL")
                .countryName("Poland")
                .isHeadquarter(null)
                .build();
    }

    // getSwiftCodeById()

    @Test
    void getSwiftCodeById_returnsSuccess_whenSwiftCodeIsHeadquarter() {
        // given
        String swiftCodeId = "COBANK12XXX";
        SwiftCode headquarter = new SwiftCode(swiftCodeId, "Bank HQ", "HQ Address", "PL", "Poland", true, "COBANK12");
        List<SwiftCode> branches = List.of(
                new SwiftCode("COBANK1234X", "Branch 1", "Addr 1", "PL", "Poland", false, "COBANK12"),
                new SwiftCode("COBANK1212X", "Branch 2", "Addr 2", "PL", "Poland", false, "COBANK12"));
        when(repository.findById(swiftCodeId)).thenReturn(Optional.of(headquarter));
        when(repository.findByHeadquarterCode("COBANK12")).thenReturn(branches);

        // when
        Result<?> result = swiftCodeService.getSwiftCodeById(swiftCodeId);

        // then
        assertTrue(result.isSuccess());
        Object data = result.getData();
        assertNotNull(data);
        assertInstanceOf(SwiftCodeHeadquarterResponse.class, data);
        SwiftCodeHeadquarterResponse response = (SwiftCodeHeadquarterResponse) data;
        assertEquals(swiftCodeId, response.getSwiftCode());
        assertEquals(2, response.getBranches().size());
    }

    @Test
    void getSwiftCodeById_returnsSuccess_whenSwiftCodeIsBranch() {
        // given
        String swiftCodeId = "BRANCH12345";
        SwiftCode branch =
                new SwiftCode(swiftCodeId, "Bank Branch", "Branch Address", "PL", "Poland", false, "HQ000011");
        when(repository.findById(swiftCodeId)).thenReturn(Optional.of(branch));

        // when
        Result<?> result = swiftCodeService.getSwiftCodeById(swiftCodeId);

        // then
        assertTrue(result.isSuccess());
        Object data = result.getData();
        assertNotNull(data);
        assertInstanceOf(SwiftCodeBranchResponse.class, data);
        SwiftCodeBranchResponse response = (SwiftCodeBranchResponse) data;
        assertEquals(swiftCodeId, response.getSwiftCode());
    }

    @Test
    void getSwiftCodeById_returnsFailure_whenSwiftCodeNotFound() {
        // given
        String swiftCodeId = "NONEXISTENT";
        when(repository.findById(swiftCodeId)).thenReturn(Optional.empty());

        // when
        Result<?> result = swiftCodeService.getSwiftCodeById(swiftCodeId);

        // then
        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertInstanceOf(SwiftCodeError.SwiftCodeNotFoundById.class, result.getError());
    }

    // getSwiftCodesByCountry()

    @Test
    void getSwiftCodesByCountry_returnsSuccess_whenRecordsExist() {
        // given
        String countryISO2 = "PL";
        SwiftCode record1 =
                new SwiftCode("CODE1234XXX", "Bank One", "Addr One", countryISO2, "Poland", true, "CODE1234");
        SwiftCode record2 =
                new SwiftCode("CODE1234YYY", "Bank Two", "Addr Two", countryISO2, "Poland", false, "CODE1234");
        when(repository.findByCountryISO2(countryISO2)).thenReturn(List.of(record1, record2));

        // when
        Result<?> result = swiftCodeService.getSwiftCodesByCountry(countryISO2);

        // then
        assertTrue(result.isSuccess());
        Object data = result.getData();
        assertNotNull(data);
        assertInstanceOf(CountrySwiftCodesResponse.class, data);
        CountrySwiftCodesResponse response = (CountrySwiftCodesResponse) data;
        assertEquals(countryISO2, response.getCountryISO2());
        assertEquals("Poland", response.getCountryName());
        assertEquals(2, response.getSwiftCodes().size());
    }

    @Test
    void getSwiftCodesByCountry_returnsFailure_whenNoRecordsExist() {
        // given
        String countryISO2 = "US";
        when(repository.findByCountryISO2(countryISO2)).thenReturn(Collections.emptyList());

        // when
        Result<?> result = swiftCodeService.getSwiftCodesByCountry(countryISO2);

        // then
        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertInstanceOf(SwiftCodeError.CountryNotFoundByCountryISO2.class, result.getError());
    }

    // addSwiftCode()

    @Test
    void addSwiftCode_returnsSuccess_whenRequestIsValid() {
        // given
        when(repository.existsById(validRequest.getSwiftCode())).thenReturn(false);
        when(repository.save(any(SwiftCode.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Result<ApiResponse> result = swiftCodeService.addSwiftCode(validRequest);

        // then
        assertTrue(result.isSuccess());
        assertEquals("swift code created successfully", result.getData().message());
        verify(repository, times(1)).save(any(SwiftCode.class));
        verify(repository, times(1)).save(argThat(SwiftCode::isHeadquarter));
    }

    @Test
    void addSwiftCode_returnsFailure_whenSwiftCodeAlreadyExists() {
        // given
        when(repository.existsById(validRequest.getSwiftCode())).thenReturn(true);

        // when
        Result<ApiResponse> result = swiftCodeService.addSwiftCode(validRequest);

        // then
        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertInstanceOf(SwiftCodeError.SwiftCodeIdExists.class, result.getError());
    }

    // deleteSwiftCode()

    @Test
    void deleteSwiftCode_deletesRecord_whenRecordExists() {
        // given
        String swiftCodeId = "TEST1234XXX";
        when(repository.existsById(swiftCodeId)).thenReturn(true);

        // when
        Result<ApiResponse> result = swiftCodeService.deleteSwiftCode(swiftCodeId);

        // then
        assertTrue(result.isSuccess());
        assertEquals("swift code deleted successfully", result.getData().message());
        verify(repository, times(1)).deleteById(swiftCodeId);
    }

    @Test
    void deleteSwiftCode_returnsFailure_whenRecordNotFound() {
        // given
        String swiftCodeId = "NONEXISTENT";
        when(repository.existsById(swiftCodeId)).thenReturn(false);

        // when
        Result<ApiResponse> result = swiftCodeService.deleteSwiftCode(swiftCodeId);

        // then
        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertInstanceOf(SwiftCodeError.SwiftCodeNotFoundById.class, result.getError());
    }
}
