package pl.iodkovskaya.leaveRequestSystem.model.entity.request;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

import java.time.LocalDate;

@Entity
@Table(name = "requests")
@Getter
@NoArgsConstructor
public class RequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    public RequestEntity(UserEntity employee, RequestStatus status, LocalDate startDate, LocalDate endDate) {
        this.employee = employee;
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
