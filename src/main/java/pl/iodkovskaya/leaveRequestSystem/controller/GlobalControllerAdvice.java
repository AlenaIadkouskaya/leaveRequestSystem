package pl.iodkovskaya.leaveRequestSystem.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.iodkovskaya.leaveRequestSystem.model.dto.MessageResponse;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.ErrorCode;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<MessageResponse> handleNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse(e.getMessage(), ErrorCode.NOT_FOUND));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageResponse> handleValidationDatesExceptions(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse(e.getMessage(), ErrorCode.DATE_PROBLEM));
    }
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<MessageResponse> handleValidationExistingExceptions(DateTimeParseException  e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse("Incorrect date.", ErrorCode.DATE_PROBLEM));
    }
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<MessageResponse> handleValidationEmptyInputDataExceptions(NullPointerException  e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse(e.getMessage(), ErrorCode.DATE_PROBLEM));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<MessageResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        MessageResponse response = new MessageResponse(
                "Duplicate entry for employee and year. Each employee can have only one record per year.",
                ErrorCode.DUPLICATE_ENTRY
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
