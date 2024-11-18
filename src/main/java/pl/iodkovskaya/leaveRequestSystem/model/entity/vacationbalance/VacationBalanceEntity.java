package pl.iodkovskaya.leaveRequestSystem.model.entity.vacationbalance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

import java.time.LocalDate;

@Entity
@Table(name = "vacation_balances", uniqueConstraints = {
        @UniqueConstraint(columnNames = "user_id")
})
@Getter
@AllArgsConstructor
public class VacationBalanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_vacation_balance_user"))
    private UserEntity user;

    @Column(name = "total_days", nullable = false)
    private Integer totalDays;

    @Column(name = "used_days", nullable = false)
    private Integer usedDays;

    @Column(name = "remaining_days", nullable = false)
    private Integer remainingDays;
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Version
    private Long version;

    public VacationBalanceEntity(UserEntity user, Integer totalDays, Integer usedDays, LocalDate hireDate) {
        this.user = user;
        this.totalDays = totalDays;
        this.usedDays = usedDays;
        this.remainingDays = totalDays - usedDays;
        this.hireDate = hireDate;
    }

    protected VacationBalanceEntity() {
    }

    public VacationBalanceEntity(Long id, UserEntity user, Integer totalDays, Integer usedDays, Integer remainingDays, LocalDate hireDate) {
        this.id = id;
        this.user = user;
        this.totalDays = totalDays;
        this.usedDays = usedDays;
        this.remainingDays = remainingDays;
        this.hireDate = hireDate;
    }

    private void updateRemainderDays() {
        this.remainingDays = this.totalDays - this.usedDays;
    }

    public void increaseUsedDays(int durationVacation) {
        this.usedDays = this.usedDays + durationVacation;
        updateRemainderDays();
    }

    public void decreaseUsedDays(int durationVacation) {
        if (durationVacation > usedDays) {
            this.usedDays = 0;
        } else {
            this.usedDays = this.usedDays - durationVacation;
        }

        updateRemainderDays();
    }

    public void incrementTotalDays(int days) {
        this.totalDays += days;
        updateRemainderDays();
    }
}
