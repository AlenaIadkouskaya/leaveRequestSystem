package pl.iodkovskaya.leaveRequestSystem.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import pl.iodkovskaya.leaveRequestSystem.model.entity.ApprovalLogEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.ApprovalLogRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LogServiceImpl implements LogService {
    private final ApprovalLogRepository approvalLogRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logApprovalAttempt(UUID leaveRequestId, Long managerId, String action) {
        ApprovalLogEntity log = new ApprovalLogEntity(leaveRequestId, managerId, action, LocalDateTime.now());
        approvalLogRepository.save(log);
    }
}
