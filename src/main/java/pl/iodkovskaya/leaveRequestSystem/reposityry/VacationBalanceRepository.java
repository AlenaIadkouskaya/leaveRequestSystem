/*
 * Copyright 2024 Alena Iadkouskaya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
