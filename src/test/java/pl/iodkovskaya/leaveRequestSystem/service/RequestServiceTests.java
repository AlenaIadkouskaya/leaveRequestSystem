package pl.iodkovskaya.leaveRequestSystem.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.iodkovskaya.leaveRequestSystem.exception.InvalidOperationException;
import pl.iodkovskaya.leaveRequestSystem.exception.StatusException;
import pl.iodkovskaya.leaveRequestSystem.mapper.RequestMapper;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestResponseDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.RequestRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTests {

    private RequestMapper requestMapper = Mockito.mock(RequestMapper.class);
    private final RequestRepository requestRepository = Mockito.mock(RequestRepository.class);

    private final UserService userService = Mockito.mock(UserService.class);

    private final VacationBalanceService vacationBalanceService = Mockito.mock(VacationBalanceService.class);
    @InjectMocks
    private RequestServiceImpl requestService;
    //@Mock
    private UserEntity mockUser = Mockito.mock(UserEntity.class);


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

    @Test
    public void should_reject_request_successfully() {
        // given
        UUID technicalId = UUID.randomUUID();
        String userEmail = "user@example.com";
        RequestEntity request = new RequestEntity(new UserEntity(), RequestStatus.PENDING, LocalDate.now(), LocalDate.now().plusDays(5));
        when(requestRepository.findByTechnicalId(technicalId)).thenReturn(Optional.of(request));

        // when
        requestService.rejectRequest(userEmail, technicalId);

        // then
        assertThat(request.getStatus()).isEqualTo(RequestStatus.REJECTED);
        verify(requestRepository).findByTechnicalId(technicalId);
        //verify(requestRepository).save(request);
        //verify(requestService).updateVacationBalance(request.getUser(), request);
        // dobrze bym było żeby tutaj był nie mock
    }

    @Test
    public void should_throw_exception_when_request_not_found() {
        // given
        UUID technicalId = UUID.randomUUID();
        String userEmail = "user@example.com";
        when(requestRepository.findByTechnicalId(technicalId)).thenReturn(Optional.empty());

        // when
        Executable e = () -> requestService.rejectRequest(userEmail, technicalId);

        // then
        assertThrows(EntityNotFoundException.class, e);
    }

    @Test
    public void should_throw_status_exception_when_request_already_rejected() {
        // given
        RequestEntity request = new RequestEntity(new UserEntity(), RequestStatus.REJECTED, LocalDate.now(), LocalDate.now().plusDays(5));
        UUID technicalId = UUID.randomUUID();
        String userEmail = "user@example.com";
        when(requestRepository.findByTechnicalId(technicalId)).thenReturn(Optional.of(request));

        // when
        Executable e = () -> requestService.rejectRequest(userEmail, technicalId);

        // then
        assertThrows(StatusException.class, e);
    }

    @Test
    public void should_update_vacation_balance_when_status_is_rejected() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // given
        RequestEntity request = new RequestEntity(new UserEntity(), RequestStatus.PENDING, LocalDate.now(), LocalDate.now().plusDays(5));
        UUID technicalId = UUID.randomUUID();
        String userEmail = "user@example.com";
        when(requestRepository.findByTechnicalId(technicalId)).thenReturn(Optional.of(request));

        // when
        requestService.rejectRequest(userEmail, technicalId);

        // then
        //verify(requestService).updateVacationBalance(mockUser, request);
        Method method = RequestServiceImpl.class.getDeclaredMethod("updateVacationBalance", UserEntity.class, RequestEntity.class);
        method.setAccessible(true);
        method.invoke(requestService, mockUser, request);//!!!
    }

    @Test
    void should_return_list_of_all_requests() {
        // given
        UserEntity user = new UserEntity();
        RequestEntity request1 = new RequestEntity(user, RequestStatus.PENDING, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 5));
        RequestEntity request2 = new RequestEntity(user, RequestStatus.APPROVED, LocalDate.of(2023, 2, 1), LocalDate.of(2023, 2, 5));
        RequestResponseDto dto1 = new RequestResponseDto(UUID.randomUUID(), "John Doe", request1.getStatus(), request1.getStartDate(), request1.getEndDate());
        RequestResponseDto dto2 = new RequestResponseDto(UUID.randomUUID(), "John Doe", request2.getStatus(), request2.getStartDate(), request2.getEndDate());

        when(requestRepository.findAll()).thenReturn(List.of(request1, request2));
        when(requestMapper.fromEntity(request1)).thenReturn(dto1);
        when(requestMapper.fromEntity(request2)).thenReturn(dto2);

        // when
        List<RequestResponseDto> result = requestService.getAllRequests();

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)).isEqualTo(dto1);
        assertThat(result.get(1)).isEqualTo(dto2);
    }

    @Test
    void should_return_request_by_id_when_request_exists() {
        // given
        UUID testUUID = UUID.randomUUID();
        UserEntity user = new UserEntity();
        RequestEntity requestEntity = new RequestEntity(user, RequestStatus.PENDING, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 5));
        when(requestRepository.findByTechnicalId(testUUID)).thenReturn(Optional.of(requestEntity));
        RequestResponseDto requestResponseDto = new RequestResponseDto(
                testUUID,
                "John Doe",
                RequestStatus.PENDING,
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 5)
        );
        when(requestMapper.fromEntity(requestEntity)).thenReturn(requestResponseDto);

        // when
        RequestResponseDto result = requestService.getRequestById(testUUID);

        // then
        assertThat(result).isEqualTo(requestResponseDto);
    }

    @Test
    void should_throw_exception_when_request_does_not_exist() {
        // given
        UUID testUUID = UUID.randomUUID();
        when(requestRepository.findByTechnicalId(testUUID)).thenReturn(Optional.empty());

        // when
        Executable e = () -> requestService.getRequestById(testUUID);

        // then
        assertThrows(EntityNotFoundException.class, e);
    }

}
