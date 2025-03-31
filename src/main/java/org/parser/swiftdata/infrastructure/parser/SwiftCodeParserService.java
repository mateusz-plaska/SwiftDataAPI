package org.parser.swiftdata.infrastructure.parser;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import java.io.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.parser.swiftdata.facade.domain.SwiftCode;
import org.parser.swiftdata.facade.domain.SwiftCodeRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
class SwiftCodeParserService {

    private final SwiftCodeRepository repository;

    public void parseAndStoreSwiftCodes(File file) {
        log.info("Parsing swift codes from file: {}", file.getAbsolutePath());
        int processedRecords = 0;
        int skippedRecords = 0;

        try (FileReader fileReader = new FileReader(file);
                CSVReader csvReader = new CSVReaderBuilder(fileReader)
                        .withCSVParser(new CSVParserBuilder().withSeparator(',').build())
                        .build()) {

            String[] headers = csvReader.readNextSilently();
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length < 7) {
                    log.error("Record does not contain enough columns: {}. Skipping.", String.join(",", record));
                    skippedRecords++;
                    continue;
                }

                try {
                    storeSwiftCodeRecord(record);
                    processedRecords++;
                } catch (Exception e) {
                    log.error(
                            "Error parsing record {}: {}. Skipping this record.",
                            String.join(",", record),
                            e.getMessage());
                    skippedRecords++;
                }
            }
        } catch (IOException | CsvValidationException e) {
            log.error("Error reading CSV file: {}", e.getMessage(), e);
        }
        log.info("Finished parsing swift codes. Processed: {}, Skipped: {}", processedRecords, skippedRecords);
    }

    private void storeSwiftCodeRecord(String[] record) {
        String swiftCode = record[1];
        String bankName = record[3];
        String address = record[4];
        String countryIso2 = record[0].toUpperCase().trim();
        String countryName = record[6].toUpperCase().trim();

        if (swiftCode.length() != 11) {
            throw new IllegalArgumentException("Swift code length is not equal 11: " + swiftCode);
        }

        boolean isHeadquarter = swiftCode.endsWith("XXX");
        String headquarterCode = swiftCode.substring(0, swiftCode.length() - 3);

        SwiftCode swiftCodeEntity =
                new SwiftCode(swiftCode, bankName, address, countryIso2, countryName, isHeadquarter, headquarterCode);
        repository.save(swiftCodeEntity);
    }
}
