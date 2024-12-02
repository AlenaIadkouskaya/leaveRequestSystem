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

import jakarta.persistence.EntityNotFoundException;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.iodkovskaya.leaveRequestSystem.exception.InvalidOperationException;
import pl.iodkovskaya.leaveRequestSystem.model.dto.VacationBalanceDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.vacationbalance.VacationBalanceEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.VacationBalanceRepository;

import java.time.LocalDate;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VacationBalanceServiceTests {

    private final UserService userService = Mockito.mock(UserService.class);
    private final VacationBalanceRepository vacationBalanceRepository = Mockito.mock(VacationBalanceRepository.class);
    private final Logger logger = Mockito.mock(Logger.class);
    @InjectMocks
    private VacationBalanceServiceImpl vacationBalanceService;

    @Test
    public void should_create_vacation_balance_entity_with_success() {
        // given
        VacationBalanceDto vacationBalanceDto = new VacationBalanceDto(1L, 20, 5, LocalDate.of(2023, 6, 18));
        UserEntity userEntity = new UserEntity("login", "1", "a@gmail.com");
        when(userService.findUserById(vacationBalanceDto.getUserId())).thenReturn(userEntity);
        // when
        vacationBalanceService.addRecord(vacationBalanceDto);
        // then
        verify(vacationBalanceRepository).save(argThat(entity ->
                entity.getUser().equals(userEntity) &&
                        entity.getTotalDays() == 20 &&
                        entity.getUsedDays() == 5
        ));
        verify(vacationBalanceRepository, times(1)).save(any(VacationBalanceEntity.class));
    }

    @Test
    public void should_throw_exception_when_user_not_found() {
        // given
        VacationBalanceDto vacationBalanceDto = new VacationBalanceDto(1L, 20, 5, LocalDate.of(2020, 8, 9));
        when(userService.findUserById(vacationBalanceDto.getUserId())).thenReturn(null);
        // when
        Executable e = () -> vacationBalanceService.addRecord(vacationBalanceDto);
        // then
        assertThrows(EntityNotFoundException.class, e);
    }

    @Test
    public void should_pass_check_when_remainder_for_user_has_sufficient_days() {
        // given
        UserEntity userEntity = new UserEntity("login", "1", "a@gmail.com");
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(1L, userEntity, 10, 0, 10, LocalDate.of(2024, 11, 18));
        when(vacationBalanceRepository.findByUser(userEntity)).thenReturn(Optional.of(vacationBalance));

        // when
        vacationBalanceService.checkRemainderForUser(userEntity, 5);

        // then
        verify(vacationBalanceRepository, times(1)).findByUser(userEntity);
    }

    @Test
    public void should_throw_exception_when_remainder_for_user_less() {
        // given
        UserEntity userEntity = new UserEntity("login", "1", "a@gmail.com");
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(1L, userEntity, 3, 0, 3, LocalDate.of(2023, 6, 18));
        when(vacationBalanceRepository.findByUser(userEntity)).thenReturn(Optional.of(vacationBalance));

        // when
        Executable e = () -> vacationBalanceService.checkRemainderForUser(userEntity, 5);

        // then
        assertThrows(InvalidOperationException.class, e);
        verify(vacationBalanceRepository, times(1)).findByUser(userEntity);
    }

    @Test
    public void should_throw_exception_when_no_balance_data_available() {
        // given
        UserEntity userEntity = new UserEntity("login", "1", "a@gmail.com");
        when(vacationBalanceRepository.findByUser(userEntity)).thenReturn(Optional.empty());

        // when
        Executable e = () -> vacationBalanceService.checkRemainderForUser(userEntity, 5);

        // then
        assertThrows(InvalidOperationException.class, e);
        verify(vacationBalanceRepository, times(1)).findByUser(userEntity);
    }

    @Test
    public void should_update_used_days_with_existing_balance() {
        // given
        UserEntity userEntity = new UserEntity("login", "1", "a@gmail.com");
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(1L, userEntity, 10, 0, 10, LocalDate.of(2023, 6, 18));
        RequestEntity request = new RequestEntity(userEntity, RequestStatus.CREATED, LocalDate.now(), LocalDate.now().plusDays(5));
        when(vacationBalanceRepository.findByUser(userEntity)).thenReturn(Optional.of(vacationBalance));
        int durationVacation = 6;

        // when
        vacationBalanceService.decreaseRemainder(userEntity, request);

        // then
        verify(vacationBalanceRepository, times(1)).findByUser(userEntity);
        assertThat(vacationBalance.getUsedDays()).isEqualTo(durationVacation);
    }

    @Test
    public void should_throw_exception_before_updating_remainder_when_no_balance() {
        // given
        UserEntity userEntity = new UserEntity("login", "1", "a@gmail.com");
        RequestEntity request = new RequestEntity(userEntity, RequestStatus.CREATED, LocalDate.now(), LocalDate.now().plusDays(5));
        when(vacationBalanceRepository.findByUser(userEntity)).thenReturn(Optional.empty());

        // when
        Executable e = () -> vacationBalanceService.increaseRemainder(userEntity, request);

        // then
        assertThrows(InvalidOperationException.class, e);
        verify(vacationBalanceRepository, times(1)).findByUser(userEntity);
    }

    @Test
    void should_increment_vacation_days_monthly_successfully() {
        LogCaptor logCaptor = LogCaptor.forClass(VacationBalanceServiceImpl.class);

        // given
        VacationBalanceEntity balance1 = mock(VacationBalanceEntity.class);
        when(balance1.getUser()).thenReturn(new UserEntity(1L, "user_1@gmail.com"));

        VacationBalanceEntity balance2 = mock(VacationBalanceEntity.class);
        when(balance2.getUser()).thenReturn(new UserEntity(2L, "user_2@gmail.com"));

        List<VacationBalanceEntity> balances = Arrays.asList(balance1, balance2);

        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();
        boolean isLeapYear = Year.isLeap(today.getYear());
        when(vacationBalanceRepository.findAllByHireDateMonthAndDay(day, isLeapYear)).thenReturn(balances);

        // when
        vacationBalanceService.incrementVacationDaysMonthly();

        // then
        verify(balance1, times(1)).incrementTotalDays(any(Integer.class));
        verify(balance2, times(1)).incrementTotalDays(any(Integer.class));
        verify(vacationBalanceRepository, times(2)).save(any(VacationBalanceEntity.class));

        List<String> logs = logCaptor.getInfoLogs();
        assertThat(logs.size()).isEqualTo(2);
        assertThat(logs.get(0)).contains("Successfully updated vacation balance for user with ID: 1");
        assertThat(logs.get(1)).contains("Successfully updated vacation balance for user with ID: 2");
    }

}
