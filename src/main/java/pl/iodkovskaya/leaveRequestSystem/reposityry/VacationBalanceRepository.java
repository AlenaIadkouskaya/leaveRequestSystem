package pl.iodkovskaya.leaveRequestSystem.reposityry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.iodkovskaya.leaveRequestSystem.model.vacationbalance.VacationBalanceEntity;

@Repository
public interface VacationBalanceRepository extends JpaRepository<VacationBalanceEntity, Long> {
}
