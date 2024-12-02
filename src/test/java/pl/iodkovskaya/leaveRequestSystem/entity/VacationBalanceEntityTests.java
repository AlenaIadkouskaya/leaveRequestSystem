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
package pl.iodkovskaya.leaveRequestSystem.entity;

import org.junit.jupiter.api.Test;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.vacationbalance.VacationBalanceEntity;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class VacationBalanceEntityTests {
    @Test
    void should_increment_total_days() {
        // given
        UserEntity user = new UserEntity("john_doe", "hashedpassword", "Doe", "John", "john.doe@example.com", null, true);
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(user, 20, 5, LocalDate.of(2023, 6, 18));

        // when
        vacationBalance.incrementTotalDays(5);

        // then
        assertThat(vacationBalance.getTotalDays()).isEqualTo(25);
        assertThat(vacationBalance.getUsedDays()).isEqualTo(5);
        assertThat(vacationBalance.getRemainingDays()).isEqualTo(20);
    }

    @Test
    void should_increment_total_days_when_no_days_left() {
        // given
        UserEntity user = new UserEntity("john_doe", "hashedpassword", "Doe", "John", "john.doe@example.com", null, true);
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(user, 10, 10, LocalDate.of(2023, 6, 18));

        // when
        vacationBalance.incrementTotalDays(5);

        // then
        assertThat(vacationBalance.getTotalDays()).isEqualTo(15);
        assertThat(vacationBalance.getUsedDays()).isEqualTo(10);
        assertThat(vacationBalance.getRemainingDays()).isEqualTo(5);
    }

    @Test
    void should_increase_used_days() {
        // given
        UserEntity user = new UserEntity("john_doe", "hashedpassword", "John", "Doe", "john.doe@example.com", null, true);
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(user, 20, 5, LocalDate.of(2023, 10, 18));


        // when
        vacationBalance.increaseUsedDays(5);

        // then
        assertThat(vacationBalance.getUsedDays()).isEqualTo(10);
        assertThat(vacationBalance.getRemainingDays()).isEqualTo(10);
    }

    @Test
    void should_decrease_used_days() {
        // given
        UserEntity user = new UserEntity("john_doe", "hashedpassword", "John", "Doe", "john.doe@example.com", null, true);
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(user, 20, 5, LocalDate.of(2023, 6, 5));

        // when
        vacationBalance.decreaseUsedDays(3);

        // then
        assertThat(vacationBalance.getUsedDays()).isEqualTo(2);
        assertThat(vacationBalance.getRemainingDays()).isEqualTo(18);
    }

    @Test
    void should_decrease_used_days_to_zero() {
        // given
        UserEntity user = new UserEntity("john_doe", "hashedpassword", "John", "Doe", "john.doe@example.com", null, true);
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(user, 20, 5, LocalDate.of(2023, 6, 18));

        // when
        vacationBalance.decreaseUsedDays(5);

        // then
        assertThat(vacationBalance.getUsedDays()).isEqualTo(0);
        assertThat(vacationBalance.getRemainingDays()).isEqualTo(20);
    }

    @Test
    void should_decrease_used_days_beyond_zero() {
        // given
        UserEntity user = new UserEntity("john_doe", "hashedpassword", "John", "Doe", "john.doe@example.com", null, true);
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(user, 20, 5, LocalDate.of(2024,1,1));

        // when
        vacationBalance.decreaseUsedDays(10);

        // then
        assertThat(vacationBalance.getUsedDays()).isEqualTo(0);
        assertThat(vacationBalance.getRemainingDays()).isEqualTo(20);
    }
}
