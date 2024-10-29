package pl.iodkovskaya.leaveRequestSystem.model.timerecord;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.iodkovskaya.leaveRequestSystem.model.user.UserEntity;

import java.time.LocalDate;

@Entity
@Table(name = "time_records")
@Getter
@NoArgsConstructor
public class TimeRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity employee;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "is_worked", nullable = false)
    private Boolean isWorked;
}
