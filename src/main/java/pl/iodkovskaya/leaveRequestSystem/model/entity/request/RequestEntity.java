package pl.iodkovskaya.leaveRequestSystem.model.entity.request;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

import java.time.LocalDate;
import java.util.UUID;

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

    public RequestEntity(UserEntity user, RequestStatus status, LocalDate startDate, LocalDate endDate) {
        this.technicalId = UUID.randomUUID();
        this.user = user;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @PrePersist
    @PreUpdate
    public void validateDates() {
        if (startDate == null) {
            throw new NullPointerException("Start date can not be empty.");
        }
    }
}
