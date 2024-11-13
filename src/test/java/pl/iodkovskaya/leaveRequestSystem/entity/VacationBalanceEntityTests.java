package pl.iodkovskaya.leaveRequestSystem.entity;

import org.junit.jupiter.api.Test;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.vacationbalance.VacationBalanceEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class VacationBalanceEntityTests {
    @Test
    void should_increment_total_days() {
        // given
        UserEntity user = new UserEntity("john_doe", "hashedpassword", "Doe", "John", "john.doe@example.com", null, true);
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(user, 20, 5);

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
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(user, 10, 10);

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
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(user, 20, 5);


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
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(user, 20, 5);

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
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(user, 20, 5);

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
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(user, 20, 5);

        // when
        vacationBalance.decreaseUsedDays(10);

        // then
        assertThat(vacationBalance.getUsedDays()).isEqualTo(0);
        assertThat(vacationBalance.getRemainingDays()).isEqualTo(20);
    }
}