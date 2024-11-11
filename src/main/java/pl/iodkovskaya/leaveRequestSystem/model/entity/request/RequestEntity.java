package pl.iodkovskaya.leaveRequestSystem.model.entity.request;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.iodkovskaya.leaveRequestSystem.exception.InvalidOperationException;
import pl.iodkovskaya.leaveRequestSystem.exception.StatusException;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;
import pl.iodkovskaya.leaveRequestSystem.model.entity.role.RoleEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
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
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_requests_user"))
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    @Version
    private Long version;

    @ManyToMany(cascade = CascadeType.REMOVE)
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
            throw new NullPointerException("Start date can not be empty");
        }
        if (endDate == null) {
            throw new NullPointerException("End date cannot be null");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }

    public void updateStatus(RequestStatus status) {
        if (status == null) {
            throw new StatusException("Status cannot be empty");
        }
        this.status = status;
    }

    public void approve(UserEntity approver) {
        ensureRequestNotRejected();
        ensureRequestNotApproved();
        if (approvers.isEmpty()) {
            updateStatus(RequestStatus.PENDING);
        }
        approvers.add(approver);

        if (isFullyApproved()) {
            updateStatus(RequestStatus.APPROVED);
        }
    }

    public void reject() {
        ensureRequestNotRejected();
        updateStatus(RequestStatus.REJECTED);
        approvers.clear();
    }

    private void ensureRequestNotRejected() {
        if (this.status == RequestStatus.REJECTED) {
            throw new StatusException("This request is already rejected!");
        }
    }
    private void ensureRequestNotApproved() {
        if (this.status == RequestStatus.APPROVED) {
            throw new StatusException("This request is already approved!");
        }
    }

    private static Set<String> getListRequiredApprovalRoles() {
        return Set.of("ROLE_HR", "ROLE_MANAGER");
    }

    private boolean isFullyApproved() {
        Set<String> requiredRoles = getListRequiredApprovalRoles();
        List<String> approverRoles = approvers.stream()
                .map(UserEntity::getRole)
                .map(RoleEntity::getRoleName)
                .collect(Collectors.toList());
        if (approverRoles.size() != requiredRoles.size()) {
            return false;
        }
        if (approverRoles.containsAll(requiredRoles)) {
            return true;
        }

        String missingRoles = requiredRoles.stream()
                .filter(role -> !approverRoles.contains(role))
                .collect(Collectors.joining(", "));

        String currentRoles = String.join(", ", approverRoles);
        throw new InvalidOperationException(
                "Action rejected. Missing roles: " + missingRoles + ". Current roles: " + currentRoles
        );
    }
}
