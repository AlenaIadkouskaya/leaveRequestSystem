package pl.iodkovskaya.leaveRequestSystem.model.vacationbalance;

import jakarta.persistence.*;
import pl.iodkovskaya.leaveRequestSystem.model.user.UserEntity;

@Entity
@Table(name = "vacation_balances")
public class VacationBalanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity employee;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "total_days", nullable = false)
    private Integer totalDays;

    @Column(name = "used_days", nullable = false)
    private Integer usedDays;

    @Column(name = "remaining_days", nullable = false)
    private Integer remainingDays;
}
