package pl.iodkovskaya.leaveRequestSystem.reposityry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.vacationbalance.VacationBalanceEntity;

@Repository
public interface VacationBalanceRepository extends JpaRepository<VacationBalanceEntity, Long> {
    VacationBalanceEntity findByUser(UserEntity user);
}
