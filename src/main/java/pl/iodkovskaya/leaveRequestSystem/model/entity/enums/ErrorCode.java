package pl.iodkovskaya.leaveRequestSystem.model.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NOT_FOUND(404, "Resource not found"),
    DATE_PROBLEM(400, "Invalid date range or date format"),
    DUPLICATE_ENTRY(409, "Duplicate entry: record already exists");
    private final int code;
    private final String message;

    @Override
    public String toString() {
        return code + ": " + message;
    }
}
