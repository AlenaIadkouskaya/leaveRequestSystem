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

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTests {
    private final RequestRepository requestRepository = Mockito.mock(RequestRepository.class);

    private final UserService userService = Mockito.mock(UserService.class);

    private final VacationBalanceService vacationBalanceService = Mockito.mock(VacationBalanceService.class);
    @InjectMocks
    private RequestServiceImpl requestService;


    @Test
    public void should_create_leave_request_successfully() {
        // given
        UserEntity userEntity = new UserEntity("login", "1", "test@example.com");
        RequestDto leaveRequestDto = new RequestDto(LocalDate.now(), 5);
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
        RequestDto leaveRequestDto = new RequestDto(LocalDate.now(), 5);
        when(userService.findUserByEmail("test@example.com")).thenReturn(null);

        // when
        Executable e = () -> requestService.createLeaveRequest("test@example.com", leaveRequestDto);

        // then
        assertThrows(EntityNotFoundException.class, e);
    }

    @Test
    public void should_throw_exception_when_insufficient_vacation_days() {
        // given
        UserEntity userEntity = new UserEntity("login", "1", "test@example.com");
        RequestDto leaveRequestDto = new RequestDto(LocalDate.now(), 5);
        when(userService.findUserByEmail("test@example.com")).thenReturn(userEntity);
        doThrow(new InvalidOperationException("Insufficient vacation days available. Your remainder: 0"))
                .when(vacationBalanceService).checkRemainderForUser(userEntity, leaveRequestDto.getDurationVacation());

        // when
        Executable e = () -> requestService.createLeaveRequest("test@example.com", leaveRequestDto);

        // then
        assertThrows(InvalidOperationException.class, e);
    }

    @Test
    public void should_throw_exception_when_overlapping_requests() {
        // given
        UserEntity userEntity = new UserEntity("login", "1", "test@example.com");
        RequestDto leaveRequestDto = new RequestDto(LocalDate.now(), 5);
        when(userService.findUserByEmail("test@example.com")).thenReturn(userEntity);
        doNothing().when(vacationBalanceService).checkRemainderForUser(userEntity, leaveRequestDto.getDurationVacation());

        RequestEntity overlappingRequest = new RequestEntity(userEntity, RequestStatus.CREATED, LocalDate.now(), LocalDate.now().plusDays(5));
        when(requestRepository.findAllByUserAndDateRange(any(), any(), any())).thenReturn(List.of(overlappingRequest));

        // when
        Executable e = () -> requestService.createLeaveRequest("test@example.com", leaveRequestDto);

        // then
        assertThrows(IllegalArgumentException.class, e);
    }

    @Test
    public void should_throw_exception_when_error_saving_leave_request() {
        // given
        UserEntity userEntity = new UserEntity("login", "1", "test@example.com");
        RequestDto leaveRequestDto = new RequestDto(LocalDate.now(), 5);
        when(userService.findUserByEmail("test@example.com")).thenReturn(userEntity);
        doNothing().when(vacationBalanceService).checkRemainderForUser(userEntity, leaveRequestDto.getDurationVacation());
        doThrow(new RuntimeException("Database error"))
                .when(requestRepository).save(any(RequestEntity.class));

        // when
        Executable e = () -> requestService.createLeaveRequest("test@example.com", leaveRequestDto);

        // then
        assertThrows(RuntimeException.class, e);
    }

    @Test
    void should_throw_exception_before_approve_when_user_not_found() {
        // given
        String userEmail = "test@example.com";
        UUID technicalId = UUID.randomUUID();
        when(userService.findUserByEmail(userEmail)).thenReturn(null);

        // when
        Executable e = () -> requestService.approveRequest(userEmail, technicalId);

        // then
        assertThrows(EntityNotFoundException.class, e);
    }

    @Test
    void should_throw_exception_before_approve_when_request_not_found() {
        // given
        String userEmail = "test@example.com";
        UserEntity approver = new UserEntity();
        UUID technicalId = UUID.randomUUID();
        when(userService.findUserByEmail(userEmail)).thenReturn(approver);
        when(requestRepository.findByTechnicalId(technicalId)).thenReturn(Optional.empty());

        // when
        Executable e = () -> requestService.approveRequest(userEmail, technicalId);

        // then
        assertThrows(EntityNotFoundException.class, e);
    }

    @Test
    void should_approve_request_successfully() throws AccessDeniedException {
        // given
        String userEmail = "test@example.com";
        UserEntity approver = new UserEntity();
        UUID technicalId = UUID.randomUUID();
        RequestEntity request = mock(RequestEntity.class);
        when(userService.findUserByEmail(userEmail)).thenReturn(approver);
        when(requestRepository.findByTechnicalId(technicalId)).thenReturn(Optional.of(request));

        // when
        requestService.approveRequest(userEmail, technicalId);

        // then
        verify(request).approve(approver);
        verify(requestRepository).findByTechnicalId(technicalId);
    }

}
