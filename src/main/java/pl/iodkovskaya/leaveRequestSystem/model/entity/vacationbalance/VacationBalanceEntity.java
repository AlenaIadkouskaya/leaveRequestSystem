package pl.iodkovskaya.leaveRequestSystem.model.entity.vacationbalance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "total_days", nullable = false)
    private Integer totalDays;

    @Column(name = "used_days", nullable = false)
    private Integer usedDays;

    @Column(name = "remaining_days", nullable = false)
    private Integer remainingDays;

    public VacationBalanceEntity(UserEntity user, Integer totalDays, Integer usedDays) {
        this.user = user;
        this.totalDays = totalDays;
        this.usedDays = usedDays;
        this.remainingDays = totalDays - usedDays;
    }

    protected VacationBalanceEntity() {

    }

    public void updateRemainderDays() {
        this.remainingDays = this.totalDays - this.usedDays;
    }

    public void increaseUsedDays(int durationVacation) {
        this.usedDays = this.usedDays + durationVacation;
        updateRemainderDays();
    }

    public void decreaseUsedDays(int durationVacation) {
        this.usedDays = this.usedDays - durationVacation;
        updateRemainderDays();
    }
}
