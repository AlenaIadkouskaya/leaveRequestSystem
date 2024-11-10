package pl.iodkovskaya.leaveRequestSystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.iodkovskaya.leaveRequestSystem.model.entity.ApprovalLogEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.ApprovalLogRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LogServiceTests {
    private final ApprovalLogRepository logRepository = Mockito.mock(ApprovalLogRepository.class);
    @InjectMocks
    private LogServiceImpl logService;

    @Test
    void should_create_log_approval_successfully_when_reject_an_request() {
        // given
        UUID testRequestId = UUID.randomUUID();
        Long testUserId = 123L;
        ApprovalLogEntity logEntity = new ApprovalLogEntity(testRequestId, testUserId, "REJECT", LocalDateTime.now());

        // when
        logService.logApprovalAttempt(testRequestId, testUserId, "REJECT");

        // then
        verify(logRepository, times(1)).save(any(ApprovalLogEntity.class));
    }
    @Test
    void should_create_log_approval_successfully_when_approve_an_request() {
        // given
        UUID testRequestId = UUID.randomUUID();
        Long testUserId = 123L;
        ApprovalLogEntity logEntity = new ApprovalLogEntity(testRequestId, testUserId, "APPROVE", LocalDateTime.now());

        // when
        logService.logApprovalAttempt(testRequestId, testUserId, "APPROVE");

        // then
        verify(logRepository, times(1)).save(any(ApprovalLogEntity.class));
    }
}
