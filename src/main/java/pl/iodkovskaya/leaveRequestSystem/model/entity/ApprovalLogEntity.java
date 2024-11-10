package pl.iodkovskaya.leaveRequestSystem.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "approval_logs")

@NoArgsConstructor
@Getter
public class ApprovalLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "action", nullable = false, length = 20)
    private String action;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public ApprovalLogEntity(UUID leaveRequestId, Long managerId, String action, LocalDateTime timestamp) {
        this.requestId = leaveRequestId;
        this.userId = managerId;
        this.action = action;
        this.timestamp = timestamp;
    }
}
