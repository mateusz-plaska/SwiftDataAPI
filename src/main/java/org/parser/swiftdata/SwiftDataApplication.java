package org.parser.swiftdata;

import java.io.File;
import org.parser.swiftdata.infrastructure.parser.SwiftCodeParserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SwiftDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwiftDataApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadSwiftData(
            SwiftCodeParserService swiftCodeParserService, @Value("${data.filepath}") String dataFilepath) {
        return (args) -> {
            swiftCodeParserService.parseAndStoreSwiftCodes(new File(dataFilepath));
        };
    }
}
