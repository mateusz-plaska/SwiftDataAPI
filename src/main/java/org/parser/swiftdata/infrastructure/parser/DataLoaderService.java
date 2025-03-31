package org.parser.swiftdata.infrastructure.parser;

import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
class DataLoaderService {

    private final SwiftCodeParserService swiftCodeParserService;

    public void loadSwiftCodeData(String dataFilePath) {
        File dataFile = new File(dataFilePath);
        if (!dataFile.exists() || !dataFile.canRead()) {
            log.error("Data file '{}' does not exist or is not readable", dataFilePath);
            return;
        }
        swiftCodeParserService.parseAndStoreSwiftCodes(dataFile);
    }
}
