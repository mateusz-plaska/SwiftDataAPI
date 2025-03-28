package org.parser.swiftdata.infrastructure.parser;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import java.io.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.parser.swiftdata.infrastructure.data.SwiftCode;
import org.parser.swiftdata.infrastructure.data.SwiftCodeRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SwiftCodeParserService {

    private final SwiftCodeRepository repository;

    public void parseAndStoreSwiftCodes(File file) {
        log.info("Parsing swift codes...");
        String[] record;

        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(file))
                .withCSVParser(new CSVParserBuilder().withSeparator(',').build())
                .build()) {

            String[] headers = csvReader.readNextSilently();
            while ((record = csvReader.readNext()) != null) {
                storeSwiftCodeRecord(record);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        log.info("Done parsing swift codes.");
    }

    private void storeSwiftCodeRecord(String[] record) {
        String swiftCode = record[1];
        String bankName = record[3];
        String address = record[4];
        String countryIso2 = record[0].toUpperCase();
        String countryName = record[6].toUpperCase();

        boolean isHeadquarter = swiftCode.endsWith("XXX");
        String headquarterCode = swiftCode.substring(0, swiftCode.length() - 3);

        repository.save(
                new SwiftCode(swiftCode, bankName, address, countryIso2, countryName, isHeadquarter, headquarterCode));
    }
}
