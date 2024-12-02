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
package pl.iodkovskaya.leaveRequestSystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.vacationbalance.VacationBalanceEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.UserRepository;
import pl.iodkovskaya.leaveRequestSystem.reposityry.VacationBalanceRepository;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class VacationBalanceServiceIntegrationTests {
    @Autowired
    private VacationBalanceRepository vacationBalanceRepository;
    @Autowired
    private VacationBalanceService vacationBalanceService;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        vacationBalanceRepository.deleteAll();
        userRepository.deleteAll();

    }

    @Test
    void should_increment_vacation_days_monthly_successfully() {
        // given
        LocalDate checkedDate = LocalDate.now();
        int day = checkedDate.getDayOfMonth();
        boolean isLeapYear = Year.isLeap(checkedDate.getYear());

        UserEntity user1 = new UserEntity("user_1@gmail.com", "1", "", "", "user_1@gmail.com", null, true);
        UserEntity user2 = new UserEntity("user_2@gmail.com", "1", "", "", "user_2@gmail.com", null, true);

        userRepository.save(user1);
        userRepository.save(user2);

        VacationBalanceEntity vacationBalance1 = new VacationBalanceEntity(user1, 20, 5, LocalDate.of(2024, 9, day));
        vacationBalanceRepository.save(vacationBalance1);
        VacationBalanceEntity vacationBalance2 = new VacationBalanceEntity(user2, 20, 10, LocalDate.of(2024, 11, day));
        vacationBalanceRepository.save(vacationBalance2);

        // when
        vacationBalanceService.incrementVacationDaysMonthly();

        // then
        List<VacationBalanceEntity> balances = vacationBalanceRepository.findAllByHireDateMonthAndDay(day, isLeapYear);

        assertThat(balances.size()).isEqualTo(2);
        for (VacationBalanceEntity balance : balances) {
            assertThat(balance.getTotalDays()).isEqualTo(22);
        }

    }

    @Test
    void should_not_update_vacation_balance_for_non_matching_hire_dates() {
        // given
        LocalDate checkedDate = LocalDate.now();
        int day = checkedDate.getDayOfMonth();
        boolean isLeapYear = Year.isLeap(checkedDate.getYear());

        UserEntity user1 = new UserEntity("user_1@gmail.com", "1", "", "", "user_1@gmail.com", null, true);
        UserEntity user2 = new UserEntity("user_2@gmail.com", "1", "", "", "user_2@gmail.com", null, true);

        userRepository.save(user1);
        userRepository.save(user2);

        VacationBalanceEntity vacationBalance1 = new VacationBalanceEntity(user1, 20, 5, LocalDate.of(2024, 9, 20));
        vacationBalanceRepository.save(vacationBalance1);
        VacationBalanceEntity vacationBalance2 = new VacationBalanceEntity(user2, 20, 10, LocalDate.of(2024, 11, day));
        vacationBalanceRepository.save(vacationBalance2);

        // when
        vacationBalanceService.incrementVacationDaysMonthly();

        // then
        List<VacationBalanceEntity> balances = vacationBalanceRepository.findAllByHireDateMonthAndDay(day, isLeapYear);

        assertThat(balances.size()).isEqualTo(1);
        for (VacationBalanceEntity balance : balances) {
            assertThat(balance.getTotalDays()).isEqualTo(22);
        }

        List<VacationBalanceEntity> otherBalances = vacationBalanceRepository.findAll();
        for (VacationBalanceEntity otherBalance : otherBalances) {
            if (otherBalance.getHireDate().getDayOfMonth() != day) {
                assertThat(otherBalance.getTotalDays()).isEqualTo(20);
            }
        }
    }


}
