package pl.iodkovskaya.leaveRequestSystem.model.entity.request;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.iodkovskaya.leaveRequestSystem.exception.StatusException;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;
import pl.iodkovskaya.leaveRequestSystem.model.entity.role.RoleEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "requests")
@Getter
@NoArgsConstructor
public class RequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "technical_id", nullable = false, unique = true)
    private UUID technicalId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @ManyToMany
    @JoinTable(
            name = "request_approvers",
            joinColumns = @JoinColumn(name = "request_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> approvers = new HashSet<>();

    public RequestEntity(UserEntity user, RequestStatus status, LocalDate startDate, LocalDate endDate) {
        this.technicalId = UUID.randomUUID();
        this.user = user;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.approvers = new HashSet<>();
    }

    @PrePersist
    @PreUpdate
    public void validateDates() {
        if (startDate == null) {
            throw new NullPointerException("Start date can not be empty.");
        }
    }

    public void updateStatus(RequestStatus status) {
        if (status == null) {
            throw new StatusException("Status is not correct");
        }
        this.status = status;
    }

    public void approve(UserEntity approver) {
        if (this.status == RequestStatus.REJECTED) {
            throw new StatusException("This request is already rejected!");
        }
        approvers.add(approver);
        boolean hasAllApproves = approvers.stream()
                .map(UserEntity::getRole)
                .map(RoleEntity::getRoleName)
                .collect(Collectors.toSet())
                .containsAll(getListRequiredApprovalRoles());
        if (hasAllApproves) {
            updateStatus(RequestStatus.APPROVED);
        } else if (!approvers.isEmpty()) {
            updateStatus(RequestStatus.PENDING);
        }

    }

    private static Set<String> getListRequiredApprovalRoles() {
        return Set.of("ROLE_HR", "ROLE_MANAGER");
    }
}
