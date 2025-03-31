package org.parser.swiftdata;

import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class SwiftDataApplicationTests {

    @MockBean
    private CommandLineRunner loadSwiftData;

    @Test
    void contextLoads() {}
}
