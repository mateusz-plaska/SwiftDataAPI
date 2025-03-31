package org.parser.swiftdata.facade.domain;

import static org.parser.swiftdata.infrastructure.error.HandleResult.handleError;
import static org.parser.swiftdata.infrastructure.error.HandleResult.handleResult;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.parser.swiftdata.facade.SwiftCodeService;
import org.parser.swiftdata.facade.dto.SwiftCodeRequest;
import org.parser.swiftdata.infrastructure.error.ErrorWrapper;
import org.parser.swiftdata.infrastructure.validator.SwiftCodeValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/swift-codes")
@RequiredArgsConstructor
@Slf4j
class SwiftCodeController {
    private final SwiftCodeService swiftCodeService;

    private final SwiftCodeValidator swiftCodeValidator;

    @GetMapping(path = "/{swift-code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getSwiftCodeById(
            @PathVariable("swift-code") String swiftCodeId, HttpServletRequest request) {
        return handleResult(swiftCodeService.getSwiftCodeById(swiftCodeId), HttpStatus.OK, request.getRequestURI());
    }

    @GetMapping(path = "/country/{countryISO2code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getSwiftCodesByCountry(
            @PathVariable String countryISO2code, HttpServletRequest request) {
        return handleResult(
                swiftCodeService.getSwiftCodesByCountry(countryISO2code), HttpStatus.OK, request.getRequestURI());
    }

    @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createSwiftCode(
            @Valid @RequestBody SwiftCodeRequest swiftCodeRequest, HttpServletRequest request) {
        Errors errors = new BeanPropertyBindingResult(swiftCodeRequest, "swiftCode");
        swiftCodeValidator.validate(swiftCodeRequest, errors);
        if (errors.hasErrors()) {
            return handleError(new ErrorWrapper(
                    errors.getAllErrors().toString(),
                    HttpStatus.CREATED,
                    request.getRequestURI(),
                    HttpStatus.BAD_REQUEST));
        }
        return handleResult(
                swiftCodeService.addSwiftCode(swiftCodeRequest), HttpStatus.CREATED, request.getRequestURI());
    }

    @DeleteMapping(path = "/{swift-code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteSwiftCode(
            @PathVariable("swift-code") String swiftCodeId, HttpServletRequest request) {
        return handleResult(swiftCodeService.deleteSwiftCode(swiftCodeId), HttpStatus.OK, request.getRequestURI());
    }
}
