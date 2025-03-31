package org.parser.swiftdata.infrastructure.parser;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class DataLoaderConfig {

    @Bean
    public CommandLineRunner loadSwiftData(
            DataLoaderService dataLoaderService, @Value("${data.file.path}") String dataFilePath) {
        return (args) -> dataLoaderService.loadSwiftCodeData(dataFilePath);
    }
}
