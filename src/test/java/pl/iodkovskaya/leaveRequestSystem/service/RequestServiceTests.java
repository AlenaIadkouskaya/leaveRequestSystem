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
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.RequestRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTests {
    private final RequestRepository requestRepository = Mockito.mock(RequestRepository.class);

    private final UserService userService = Mockito.mock(UserService.class);

    private final VacationBalanceService vacationBalanceService = Mockito.mock(VacationBalanceService.class);
    @InjectMocks
    private RequestServiceImpl requestService;
    private UserEntity userEntity;
    private RequestDto leaveRequestDto;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity(1L, "login", "1", "test@example.com");

        leaveRequestDto = new RequestDto(LocalDate.now(), 5);
    }

    @Test
    public void should_create_leave_request_successfully() {
        // given
        when(userService.findUserByEmail("test@example.com")).thenReturn(userEntity);
        when(requestRepository.save(any(RequestEntity.class))).thenReturn(new RequestEntity());
        doNothing().when(vacationBalanceService).checkRemainderForUser(userEntity, leaveRequestDto.getDurationVacation());

        // when
        requestService.createLeaveRequest("test@example.com", leaveRequestDto);

        // then
        verify(requestRepository, times(1)).save(any(RequestEntity.class));
        verify(vacationBalanceService, times(1)).updateRemainder(userEntity, leaveRequestDto.getDurationVacation());
    }

    @Test
    public void should_throw_exception_when_user_not_found() {
        // given
        when(userService.findUserByEmail("test@example.com")).thenReturn(null);

        // when
        Executable e = () -> requestService.createLeaveRequest("test@example.com", leaveRequestDto);

        // then
        assertThrows(EntityNotFoundException.class, e);
    }

    @Test
    public void should_throw_exception_when_insufficient_vacation_days() {
        // given
        when(userService.findUserByEmail("test@example.com")).thenReturn(userEntity);
        doThrow(new InvalidOperationException("Insufficient vacation days available. Your remainder: 0"))
                .when(vacationBalanceService).checkRemainderForUser(userEntity, leaveRequestDto.getDurationVacation());

        // when
        Executable e = () -> requestService.createLeaveRequest("test@example.com", leaveRequestDto);

        // then
        assertThrows(InvalidOperationException.class, e);
    }
//    Исключение при недостаточном количестве дней отпуска.
//    Исключение при наличии перекрывающихся запросов на отпуск.
//    Исключение при ошибке сохранения запроса на отпуск.

    @Test
    public void should_throw_exception_when_overlapping_requests() {
        // given
        when(userService.findUserByEmail("test@example.com")).thenReturn(userEntity);
        doNothing().when(vacationBalanceService).checkRemainderForUser(userEntity, leaveRequestDto.getDurationVacation());

        RequestEntity overlappingRequest = new RequestEntity(userEntity, RequestStatus.CREATED, LocalDate.now(), LocalDate.now().plusDays(5));
        when(requestRepository.findAllByEmployeeAndDateRange(any(), any(), any())).thenReturn(List.of(overlappingRequest));

        // when
        Executable e = () -> requestService.createLeaveRequest("test@example.com", leaveRequestDto);

        // then
        assertThrows(IllegalArgumentException.class, e);
    }

    @Test
    public void should_throw_exception_when_error_saving_leave_request() {
        // given
        when(userService.findUserByEmail("test@example.com")).thenReturn(userEntity);
        doNothing().when(vacationBalanceService).checkRemainderForUser(userEntity, leaveRequestDto.getDurationVacation());
        doThrow(new RuntimeException("Database error"))
                .when(requestRepository).save(any(RequestEntity.class));

        // when
        Executable e = () -> requestService.createLeaveRequest("test@example.com", leaveRequestDto);

        // then
        assertThrows(RuntimeException.class, e);
    }
}
