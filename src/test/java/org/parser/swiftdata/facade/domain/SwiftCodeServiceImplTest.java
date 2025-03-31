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
    void deleteSwiftCode_deletesSingleRecord_whenRecordExists() {
        // given
        String swiftCodeId = "TEST1234XXY";
        SwiftCode swiftCode = SwiftCode.builder()
                .swiftCode(swiftCodeId)
                .bankName("BankName")
                .address("Address")
                .countryISO2("US")
                .countryName("USA")
                .isHeadquarter(false)
                .headquarterCode("TEST1234")
                .build();
        when(repository.findById(swiftCodeId)).thenReturn(Optional.of(swiftCode));

        // when
        Result<ApiResponse> result = swiftCodeService.deleteSwiftCode(swiftCodeId);

        // then
        assertTrue(result.isSuccess());
        assertEquals(
                "swift code deleted successfully, deleted 1 record(s)",
                result.getData().message());

        verify(repository, times(1)).deleteAllById(List.of(swiftCodeId));
    }

    @Test
    void deleteSwiftCode_deletesHeadquarterAndBranches_whenRecordIsHeadquarter() {
        // given
        String headquarterId = "12345678XXX";
        String headquarterCode = "12345678";
        SwiftCode headquarter = SwiftCode.builder()
                .swiftCode(headquarterId)
                .bankName("Main Bank")
                .address("HQ Address")
                .countryISO2("US")
                .countryName("USA")
                .isHeadquarter(true)
                .headquarterCode(headquarterCode)
                .build();
        SwiftCode branch1 = SwiftCode.builder()
                .swiftCode("12345678YY1")
                .bankName("Branch1")
                .address("Address1")
                .countryISO2("US")
                .countryName("USA")
                .isHeadquarter(false)
                .headquarterCode(headquarterCode)
                .build();
        SwiftCode branch2 = SwiftCode.builder()
                .swiftCode("12345678YY2")
                .bankName("Branch2")
                .address("Address2")
                .countryISO2("US")
                .countryName("USA")
                .isHeadquarter(false)
                .headquarterCode(headquarterCode)
                .build();

        when(repository.findById(headquarterId)).thenReturn(Optional.of(headquarter));
        when(repository.findByHeadquarterCode(headquarterCode)).thenReturn(List.of(headquarter, branch1, branch2));

        // when
        Result<ApiResponse> result = swiftCodeService.deleteSwiftCode(headquarterId);

        // then
        assertTrue(result.isSuccess());
        assertEquals(
                "swift code deleted successfully, deleted 3 record(s)",
                result.getData().message());

        verify(repository, times(1))
                .deleteAllById(List.of(headquarterId, branch1.getSwiftCode(), branch2.getSwiftCode()));
    }

    @Test
    void deleteSwiftCode_returnsFailure_whenRecordNotFound() {
        // given
        String swiftCodeId = "NONEXISTENT";
        when(repository.findById(swiftCodeId)).thenReturn(Optional.empty());

        // when
        Result<ApiResponse> result = swiftCodeService.deleteSwiftCode(swiftCodeId);

        // then
        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertInstanceOf(SwiftCodeError.SwiftCodeNotFoundById.class, result.getError());

        verify(repository, never()).deleteAllById(any());
    }
}
