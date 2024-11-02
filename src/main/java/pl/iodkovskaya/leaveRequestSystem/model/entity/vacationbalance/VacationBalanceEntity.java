package pl.iodkovskaya.leaveRequestSystem.model.entity.vacationbalance;

import jakarta.persistence.*;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

@Entity
@Table(name = "vacation_balances", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "year"})
})
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

    public VacationBalanceEntity(UserEntity employee, Integer year, Integer totalDays, Integer usedDays) {
        this.employee = employee;
        this.year = year;
        this.totalDays = totalDays;
        this.usedDays = usedDays;
        this.remainingDays = totalDays - usedDays;
    }

    protected VacationBalanceEntity() {

    }
}
