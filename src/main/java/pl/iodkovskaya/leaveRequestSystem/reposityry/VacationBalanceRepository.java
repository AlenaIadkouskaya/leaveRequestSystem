package pl.iodkovskaya.leaveRequestSystem.reposityry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.vacationbalance.VacationBalanceEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface VacationBalanceRepository extends JpaRepository<VacationBalanceEntity, Long> {
    Optional<VacationBalanceEntity> findByUser(UserEntity user);

    @Query("SELECT v FROM VacationBalanceEntity v " +
            "WHERE EXTRACT(DAY FROM v.hireDate) = :day " +
            "OR (EXTRACT(MONTH FROM v.hireDate) = 2 AND EXTRACT(DAY FROM v.hireDate) = 29 " +
            "AND :isLeapYear = false " +
            "AND EXTRACT(MONTH FROM CURRENT_DATE) = 3 " +
            "AND :day = 1)")
    List<VacationBalanceEntity> findAllByHireDateMonthAndDay(int day, boolean isLeapYear);
}
