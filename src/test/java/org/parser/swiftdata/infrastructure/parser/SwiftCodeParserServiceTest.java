package org.parser.swiftdata.infrastructure.parser;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.parser.swiftdata.facade.domain.SwiftCode;
import org.parser.swiftdata.facade.domain.SwiftCodeRepository;

@ExtendWith(MockitoExtension.class)
public class SwiftCodeParserServiceTest {

    @InjectMocks
    private SwiftCodeParserService parserService;

    @Mock
    private SwiftCodeRepository repository;

    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("swift_codes", ".csv");
    }

    @AfterEach
    void tearDown() {
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void parseAndStoreSwiftCodes_validFile_callsSaveTwice() throws Exception {
        // given
        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.println("country_iso2,swift_code,unused,bank_name,address,unused,country_name");
            writer.println("PL,TEST1234XXX,foo,Test Bank,Test Address,bar,Poland");
            writer.println("PL,BRANCH45YYY,foo,Branch Bank,Branch Address,bar,Poland");
        }

        // when
        parserService.parseAndStoreSwiftCodes(tempFile);

        // then
        verify(repository, times(2)).save(any(SwiftCode.class));

        ArgumentCaptor<SwiftCode> swiftCodeCaptor = ArgumentCaptor.forClass(SwiftCode.class);
        verify(repository, times(2)).save(swiftCodeCaptor.capture());
        List<SwiftCode> savedRecords = swiftCodeCaptor.getAllValues();

        SwiftCode record1 = savedRecords.getFirst();
        assertTrue(record1.isHeadquarter());
        assertEquals("TEST1234", record1.getHeadquarterCode());

        SwiftCode record2 = savedRecords.get(1);
        assertFalse(record2.isHeadquarter());
        assertEquals("BRANCH45", record2.getHeadquarterCode());
    }

    @Test
    void parseAndStoreSwiftCodes_emptyFile_noSaveCalls() throws Exception {
        // given
        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.print("\n");
        }

        // when
        parserService.parseAndStoreSwiftCodes(tempFile);

        // then
        verify(repository, never()).save(any(SwiftCode.class));
    }

    @Test
    void parseAndStoreSwiftCodes_invalidRecords_noSaveCalls() throws Exception {
        // given
        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.println("country_iso2,swift_code,unused,bank_name,address,unused,country_name");
            writer.println("PL,INVALID,foo");
        }

        // when
        parserService.parseAndStoreSwiftCodes(tempFile);

        // then
        verify(repository, never()).save(any(SwiftCode.class));
    }

    @Test
    void parseAndStoreSwiftCodes_mixedRecords_FewSaveCalls() throws Exception {
        // given
        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.println("country_iso2,swift_code,unused,bank_name,address,unused,country_name");
            writer.println("PL,VALID123XXX,foo,Valid Bank,Valid Address,bar,Poland");
            writer.println("PL,INVALID");
        }

        // when
        parserService.parseAndStoreSwiftCodes(tempFile);

        // then
        verify(repository, times(1)).save(any(SwiftCode.class));
    }
}
