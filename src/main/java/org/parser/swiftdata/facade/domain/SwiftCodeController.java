package org.parser.swiftdata.facade.domain;

import static org.parser.swiftdata.infrastructure.error.HandleResult.handleResult;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.parser.swiftdata.facade.SwiftCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/swift-codes")
@RequiredArgsConstructor
@Slf4j
class SwiftCodeController {
    private final SwiftCodeService swiftCodeService;

    @GetMapping("/{swift-code}")
    public ResponseEntity<String> getSwiftCodeById(
            @PathVariable("swift-code") String swiftCodeId, HttpServletRequest request) {
        return handleResult(swiftCodeService.getSwiftCodeById(swiftCodeId), HttpStatus.OK, request.getRequestURI());
    }

    @GetMapping("/country/{countryISO2code}")
    public ResponseEntity<String> getSwiftCodesByCountry(
            @PathVariable String countryISO2code, HttpServletRequest request) {
        return handleResult(
                swiftCodeService.getSwiftCodesByCountry(countryISO2code), HttpStatus.OK, request.getRequestURI());
    }
}
