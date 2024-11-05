package pl.iodkovskaya.leaveRequestSystem.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.iodkovskaya.leaveRequestSystem.exception.InvalidOperationException;
import pl.iodkovskaya.leaveRequestSystem.model.dto.VacationBalanceDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.vacationbalance.VacationBalanceEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.VacationBalanceRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VacationBalanceServiceTests {

    private final UserService userService = Mockito.mock(UserService.class);
    private final VacationBalanceRepository vacationBalanceRepository = Mockito.mock(VacationBalanceRepository.class);
    @InjectMocks
    private VacationBalanceServiceImpl vacationBalanceService;
    private VacationBalanceDto vacationBalanceDto;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity("login", "1", "a@gmail.com");
    }

    @Test
    public void should_create_vacation_balance_entity_with_success() {
        // given
        vacationBalanceDto = new VacationBalanceDto(1L, 20, 5);
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
        vacationBalanceDto = new VacationBalanceDto(1L, 20, 5);
        when(userService.findUserById(vacationBalanceDto.getUserId())).thenReturn(null);
        // when
        Executable e = () -> vacationBalanceService.addRecord(vacationBalanceDto);
        // then
        assertThrows(EntityNotFoundException.class, e);
    }

    @Test
    public void should_pass_check_when_remainder_for_user_has_sufficient_days() {
        // given
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(1L, userEntity, 10, 0, 10);
        when(vacationBalanceRepository.findByUser(userEntity)).thenReturn(Optional.of(vacationBalance));

        // when
        vacationBalanceService.checkRemainderForUser(userEntity, 5);

        // then
        verify(vacationBalanceRepository, times(1)).findByUser(userEntity);
    }

    @Test
    public void should_throw_exception_when_remainder_for_user_less() {
        // given
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(1L, userEntity, 3, 0, 3);
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
        VacationBalanceEntity vacationBalance = new VacationBalanceEntity(1L, userEntity, 10, 0, 10);
        when(vacationBalanceRepository.findByUser(userEntity)).thenReturn(Optional.of(vacationBalance));

        // when
        int durationVacation = 5;
        vacationBalanceService.updateRemainder(userEntity, durationVacation);

        // then
        verify(vacationBalanceRepository, times(1)).findByUser(userEntity);
        assertThat(vacationBalance.getUsedDays()).isEqualTo(durationVacation);
    }

    @Test
    public void should_throw_exception_before_updating_remainder_when_no_balance() {
        // given
        when(vacationBalanceRepository.findByUser(userEntity)).thenReturn(Optional.empty());

        // when
        Executable e = () -> vacationBalanceService.updateRemainder(userEntity, 5);

        // then
        assertThrows(InvalidOperationException.class, e);
        verify(vacationBalanceRepository, times(1)).findByUser(userEntity);
    }
}
