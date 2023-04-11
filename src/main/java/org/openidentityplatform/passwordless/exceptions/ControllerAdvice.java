package org.openidentityplatform.passwordless.exceptions;

import lombok.extern.log4j.Log4j2;
import org.openidentityplatform.passwordless.otp.services.BadRequestException;
import org.openidentityplatform.passwordless.otp.services.SendOtpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.Map;

@RestControllerAdvice
@Log4j2
public class ControllerAdvice {

    private final static String ERROR_PROPERTY = "error";
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(Collections.singletonMap(ERROR_PROPERTY, e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SendOtpException.class)
    public ResponseEntity<Map<String, String>> handleSendOtpException(SendOtpException e) {
        return new ResponseEntity<>(Collections.singletonMap(ERROR_PROPERTY, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleSendOtpException(BadRequestException e) {
        return new ResponseEntity<>(Collections.singletonMap(ERROR_PROPERTY, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleCommonException(RuntimeException e) {
        log.error("internal server error occurred", e);
        return new ResponseEntity<>(Collections.singletonMap(ERROR_PROPERTY, "internal server error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
