package pl.iodkovskaya.leaveRequestSystem.model.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NOT_FOUND(404, "Resource not found"),
    DATE_PROBLEM(400, "Invalid date range or date format"),
    DUPLICATE_ENTRY(409, "Duplicate entry: record already exists"),
    BUSINESS_LOGIC_ERROR(400, "Business logic violation occurred"),
    ACCESS_DENIED(403, "Access denied: insufficient permissions"),
    STATUS_CONFLICT(409, "Conflict with the current status"),
    ROLE_ALREADY_EXISTS(409, "Role already exists: the specified role is duplicate");

    private final int code;
    private final String message;

    @Override
    public String toString() {
        return code + ": " + message;
    }
}
