package pl.iodkovskaya.leaveRequestSystem.service;

import java.util.UUID;

public interface LogService {
    void logApprovalAttempt(UUID leaveRequestId, Long managerId, String action);
}
