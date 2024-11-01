package pl.iodkovskaya.leaveRequestSystem.reposityry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
    @Query("SELECT r FROM RequestEntity r WHERE r.employee = :employee AND " +
            "((r.startDate BETWEEN :startDate AND :endDate) OR (r.endDate BETWEEN :startDate AND :endDate) OR " +
            "(r.startDate <= :startDate AND r.endDate >= :endDate))")
    List<RequestEntity> findAllByEmployeeAndDateRange(
            @Param("employee") UserEntity employee,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


}
