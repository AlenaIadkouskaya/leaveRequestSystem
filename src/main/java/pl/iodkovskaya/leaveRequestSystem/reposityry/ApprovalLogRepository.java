package pl.iodkovskaya.leaveRequestSystem.reposityry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.iodkovskaya.leaveRequestSystem.model.entity.ApprovalLogEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;

import java.util.UUID;

@Repository
public interface ApprovalLogRepository extends JpaRepository<ApprovalLogEntity, Long> {
    ApprovalLogEntity findByRequestId(UUID requestId);
}
