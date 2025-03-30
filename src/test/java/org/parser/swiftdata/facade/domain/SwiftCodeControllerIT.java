package org.parser.swiftdata.facade.domain;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SwiftCodeControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SwiftCodeRepository repository;

    @MockBean
    private CommandLineRunner loadSwiftData;

    @BeforeEach
    void setUp() {
        List<SwiftCode> swiftCodeData = List.of(
                SwiftCode.builder()
                        .swiftCode("NB123456XXX")
                        .bankName("Name 1")
                        .address("Hq Address 1")
                        .countryISO2("EN")
                        .countryName("England")
                        .isHeadquarter(true)
                        .headquarterCode("NB123456")
                        .build(),
                SwiftCode.builder()
                        .swiftCode("COBANK1234X")
                        .bankName("Name 2")
                        .address("Branch Address 1")
                        .countryISO2("PL")
                        .countryName("Poland")
                        .isHeadquarter(false)
                        .headquarterCode("COBANK12")
                        .build(),
                SwiftCode.builder()
                        .swiftCode("COBANK12XXX")
                        .bankName("Name 3")
                        .address("Hq Address 2")
                        .countryISO2("PL")
                        .countryName("Poland")
                        .isHeadquarter(true)
                        .headquarterCode("COBANK12")
                        .build());

        repository.deleteAll();
        repository.saveAll(swiftCodeData);
    }

    // GET /v1/swift-codes/{swift-code}

    @Test
    void getSwiftCodeById_returnsHeadquarter_whenSwiftCodeIsHeadquarter() throws Exception {
        // given
        // when
        mockMvc.perform(get("/v1/swift-codes/COBANK12XXX").accept(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("COBANK12XXX")))
                .andExpect(content().string(containsString("Name 3")))
                .andExpect(content().string(containsString("Hq Address 2")))
                .andExpect(content().string(containsString("branches")))
                .andExpect(content().string(containsString("COBANK1234X")))
                .andExpect(content().string(containsString("Name 2")));
    }

    @Test
    void getSwiftCodeById_returnsBranch_whenSwiftCodeIsBranch() throws Exception {
        // given
        // when
        mockMvc.perform(get("/v1/swift-codes/COBANK1234X").accept(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("COBANK1234X")))
                .andExpect(content().string(containsString("Name 2")))
                .andExpect(content().string(containsString("Branch Address 1")))
                .andExpect(content().string(containsString("Poland")));
    }

    @Test
    void getSwiftCodeById_returnsNotFound_whenSwiftCodeDoesNotExist() throws Exception {
        // given
        // when
        mockMvc.perform(get("/v1/swift-codes/NONEXISTENT").accept(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("not found")));
    }

    // GET /v1/swift-codes/country/{countryISO2code}

    @Test
    void getSwiftCodesByCountry_returnsBranches_whenRecordsExist() throws Exception {
        // given
        // when
        mockMvc.perform(get("/v1/swift-codes/country/PL").accept(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("PL")))
                .andExpect(content().string(containsString("Poland")))
                .andExpect(content().string(containsString("COBANK12XXX")))
                .andExpect(content().string(containsString("COBANK1234X")));
    }

    @Test
    void getSwiftCodesByCountry_returnsNotFound_whenNoRecordsForCountry() throws Exception {
        // given
        // when
        mockMvc.perform(get("/v1/swift-codes/country/US").accept(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("not found")));
    }

    // POST /v1/swift-codes/

    @Test
    void createSwiftCode_returnsCreated_whenRequestIsValid() throws Exception {
        // given
        String payload =
                """
            {
                "swiftCode": "TEST1234XXX",
                "bankName": "Test Bank",
                "address": "Test Address",
                "countryISO2": "PL",
                "countryName": "Poland",
                "isHeadquarter": true
            }
            """;

        // when
        mockMvc.perform(post("/v1/swift-codes/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                // then
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("swift code created successfully")));
    }

    @Test
    void createSwiftCode_returnsBadRequest_whenValidationFails_countryNameConflict() throws Exception {
        // given
        String payload =
                """
            {
                "swiftCode": "NEW12345XXX",
                "bankName": "New Bank",
                "address": "New Address",
                "countryISO2": "PL",
                "countryName": "FRANCE",
                "isHeadquarter": true
            }
            """;

        // when
        mockMvc.perform(post("/v1/swift-codes/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("does not match to the existing one for the countryISO2")));
    }

    @Test
    void createSwiftCode_returnsBadRequest_whenValidationFails_headquarterInconsistency() throws Exception {
        // given
        String payload =
                """
            {
                "swiftCode": "TEST1234YYY",
                "bankName": "Test Bank",
                "address": "Test Address",
                "countryISO2": "PL",
                "countryName": "Poland",
                "isHeadquarter": true
            }
            """;

        // when
        mockMvc.perform(post("/v1/swift-codes/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("does not match the provided isHeadquarter value")));
    }

    // DELETE /v1/swift-codes/{swift-code}

    @Test
    void deleteSwiftCode_returnsOk_whenRecordExists() throws Exception {
        // given
        // when
        mockMvc.perform(delete("/v1/swift-codes/COBANK1234X").accept(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("swift code deleted successfully")));
    }

    @Test
    void deleteSwiftCode_returnsNotFound_whenRecordDoesNotExist() throws Exception {
        // given
        // when
        mockMvc.perform(delete("/v1/swift-codes/NONEXISTENT"))
                // then
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("not found")));
    }
}
