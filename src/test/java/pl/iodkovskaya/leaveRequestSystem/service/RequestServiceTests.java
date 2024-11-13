package pl.iodkovskaya.leaveRequestSystem.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.iodkovskaya.leaveRequestSystem.exception.InvalidOperationException;
import pl.iodkovskaya.leaveRequestSystem.exception.StatusException;
import pl.iodkovskaya.leaveRequestSystem.mapper.RequestMapper;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestResponseDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.role.RoleEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.RequestRepository;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTests {

    private final RequestMapper requestMapper = Mockito.mock(RequestMapper.class);
    private final RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
    private final UserService userService = Mockito.mock(UserService.class);
    private final RequestListener requestListner = Mockito.mock(RequestListener.class);
    private final LogService logService = Mockito.mock(LogService.class);
    @InjectMocks
    private RequestServiceImpl requestService;
    private final UserEntity mockUser = Mockito.mock(UserEntity.class);

    @Test
    public void should_create_leave_request_successfully() {
        // given
        UserEntity userEntity = new UserEntity("login", "1", "test@example.com");
        RequestDto leaveRequestDto = new RequestDto(LocalDate.now(), 5);
        when(userService.findUserByEmail("test@example.com")).thenReturn(userEntity);
        RequestEntity requestEntity = new RequestEntity();
        when(requestRepository.save(any(RequestEntity.class))).thenReturn(requestEntity);
        doNothing().when(requestListner).checkRemainderForUser(userEntity, leaveRequestDto.getDurationVacation());

        // when
        requestService.createLeaveRequest("test@example.com", leaveRequestDto);

        // then
        verify(requestRepository, times(1)).save(any(RequestEntity.class));
        verify(requestListner, times(1)).decreaseRemainder(userEntity, requestEntity);
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
                .when(requestListner).checkRemainderForUser(userEntity, leaveRequestDto.getDurationVacation());

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
        doNothing().when(requestListner).checkRemainderForUser(userEntity, leaveRequestDto.getDurationVacation());

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
        doNothing().when(requestListner).checkRemainderForUser(userEntity, leaveRequestDto.getDurationVacation());
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
    void should_throw_exception_before_approval_when_request_is_approved() {
        // given
        String userEmail = "test@example.com";
        UserEntity approver = new UserEntity();
        UUID technicalId = UUID.randomUUID();
        RequestEntity request = new RequestEntity(new UserEntity(), RequestStatus.APPROVED, LocalDate.now(), LocalDate.now().plusDays(1));
        when(userService.findUserByEmail(userEmail)).thenReturn(approver);
        when(requestRepository.findByTechnicalId(technicalId)).thenReturn(Optional.of(request));
        doNothing().when(logService).logApprovalAttempt(technicalId, approver.getUserId(), "APPROVE");
        // when
        Executable e = () -> requestService.approveRequest(userEmail, technicalId);

        // then
        assertThrows(StatusException.class, e);
        verify(logService, times(1)).logApprovalAttempt(technicalId, approver.getUserId(), "APPROVE");
    }

    @Test
    public void should_reject_request_successfully() {
        // given
        UUID technicalId = UUID.randomUUID();
        String userEmail = "user@example.com";
        UserEntity performer = new UserEntity(1L, userEmail);
        RequestEntity request = new RequestEntity(new UserEntity(), RequestStatus.PENDING, LocalDate.now(), LocalDate.now().plusDays(5));
        when(userService.findUserByEmail(userEmail)).thenReturn(performer);
        when(requestRepository.findByTechnicalId(technicalId)).thenReturn(Optional.of(request));

        // when
        requestService.rejectRequest(userEmail, technicalId);

        // then
        assertThat(request.getStatus()).isEqualTo(RequestStatus.REJECTED);
        verify(requestRepository).findByTechnicalId(technicalId);
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
        UserEntity performer = new UserEntity(1L, userEmail);
        when(requestRepository.findByTechnicalId(technicalId)).thenReturn(Optional.of(request));
        when(userService.findUserByEmail(userEmail)).thenReturn(performer);

        // when
        Executable e = () -> requestService.rejectRequest(userEmail, technicalId);

        // then
        assertThrows(StatusException.class, e);
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

    @Test
    public void should_find_all_requests_for_logged_in_user() {
        // given
        RequestEntity request = new RequestEntity();
        RequestResponseDto requestResponse = new RequestResponseDto();
        when(userService.findUserByEmail("test@example.com")).thenReturn(mockUser);
        when(requestRepository.findByUser(mockUser)).thenReturn(List.of(request));
        when(requestMapper.fromEntity(request)).thenReturn(requestResponse);

        // when
        List<RequestResponseDto> result = requestService.getRequestsByUser("test@example.com");

        // then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(requestResponse);
        verify(userService, times(1)).findUserByEmail("test@example.com");
        verify(requestRepository, times(1)).findByUser(mockUser);
        verify(requestMapper, times(1)).fromEntity(request);
    }

    @Test
    public void should_throw_exception_when_user_not_found_while_fetching_requests() {
        // given
        when(userService.findUserByEmail("notfound@example.com")).thenReturn(null);

        // when
        Executable e = () -> requestService.getRequestsByUser("notfound@example.com");

        // then
        assertThrows(EntityNotFoundException.class, e);
        verify(userService, times(1)).findUserByEmail("notfound@example.com");
        verify(requestRepository, never()).findByUser(any());
        verify(requestMapper, never()).fromEntity(any());
    }

    @Test
    public void should_return_empty_list_requests_when_for_user_not_exists_one() {
        // given
        when(userService.findUserByEmail("test@example.com")).thenReturn(mockUser);
        when(requestRepository.findByUser(mockUser)).thenReturn(Collections.emptyList());

        // when
        List<RequestResponseDto> result = requestService.getRequestsByUser("test@example.com");

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(userService, times(1)).findUserByEmail("test@example.com");
        verify(requestRepository, times(1)).findByUser(mockUser);
        verify(requestMapper, never()).fromEntity(any());
    }

    @Test
    public void should_return_all_requests_not_approved_by_current_user() {
        // given
        RequestResponseDto requestResponseDto = new RequestResponseDto();
        UserEntity approver = new UserEntity("", "", "", "", "",
                new RoleEntity("ROLE_MANAGER", Set.of()), true);

        RequestEntity request1 = new RequestEntity(mockUser, RequestStatus.PENDING, LocalDate.of(2024, 11, 1),
                LocalDate.of(2024, 11, 1));
        request1.approve(approver);

        RequestEntity request2 = new RequestEntity(mockUser, RequestStatus.CREATED, LocalDate.of(2024, 11, 5),
                LocalDate.of(2024, 11, 5));

        List<RequestEntity> allRequests = List.of(request1, request2);

        when(requestRepository.findAllRequestsToApprove()).thenReturn(allRequests);
        when(requestMapper.fromEntity(request1)).thenReturn(requestResponseDto);
        when(requestMapper.fromEntity(request2)).thenReturn(requestResponseDto);
        when(userService.findUserByEmail(approver.getEmail())).thenReturn(approver);

        // when
        List<RequestResponseDto> result = requestService.getAllRequestsToApprove(approver.getEmail());

        // then
        assertThat(result.size()).isEqualTo(1);
        verify(requestRepository, times(1)).findAllRequestsToApprove();
        verify(requestMapper, times(1)).fromEntity(any(RequestEntity.class));
    }

    @Test
    public void should_return_all_created_requests() {
        // given
        RequestResponseDto requestResponseDto = new RequestResponseDto();
        UserEntity approver = new UserEntity("", "", "", "", "",
                new RoleEntity("ROLE_MANAGER", Set.of()), true);

        RequestEntity request1 = new RequestEntity(mockUser, RequestStatus.CREATED, LocalDate.of(2024, 11, 1),
                LocalDate.of(2024, 11, 1));

        RequestEntity request2 = new RequestEntity(mockUser, RequestStatus.CREATED, LocalDate.of(2024, 11, 5),
                LocalDate.of(2024, 11, 5));

        List<RequestEntity> allRequests = List.of(request1, request2);

        when(requestRepository.findAllRequestsToApprove()).thenReturn(allRequests);
        when(requestMapper.fromEntity(request1)).thenReturn(requestResponseDto);
        when(requestMapper.fromEntity(request2)).thenReturn(requestResponseDto);
        when(userService.findUserByEmail(approver.getEmail())).thenReturn(approver);

        // when
        List<RequestResponseDto> result = requestService.getAllRequestsToApprove(approver.getEmail());

        // then
        assertThat(result.size()).isEqualTo(2);
        verify(requestRepository, times(1)).findAllRequestsToApprove();
        verify(requestMapper, times(2)).fromEntity(any(RequestEntity.class));
    }

    @Test
    public void should_get_all_requests_to_approve_sorted_by_start_date() {
        // given
        UserEntity approver = new UserEntity("", "", "", "", "",
                new RoleEntity("ROLE_MANAGER", Set.of()), true);
        UserEntity approverDone = new UserEntity("", "", "", "", "",
                new RoleEntity("ROLE_HR", Set.of()), true);

        RequestEntity request1 = new RequestEntity(mockUser, RequestStatus.PENDING, LocalDate.of(2024, 11, 11),
                LocalDate.of(2024, 11, 11));
        request1.approve(approverDone);

        RequestEntity request2 = new RequestEntity(mockUser, RequestStatus.PENDING, LocalDate.of(2024, 11, 5),
                LocalDate.of(2024, 11, 5));
        request2.approve(approverDone);

        List<RequestEntity> allRequests = List.of(request1, request2);

        RequestResponseDto requestResponse1 = new RequestResponseDto(UUID.randomUUID(), "", RequestStatus.PENDING, request2.getStartDate(), request2.getEndDate());
        RequestResponseDto requestResponse2 = new RequestResponseDto(UUID.randomUUID(), "", RequestStatus.PENDING, request1.getStartDate(), request1.getEndDate());

        when(requestRepository.findAllRequestsToApprove()).thenReturn(allRequests);
        when(requestMapper.fromEntity(request1)).thenReturn(requestResponse2);
        when(requestMapper.fromEntity(request2)).thenReturn(requestResponse1);
        when(userService.findUserByEmail(approver.getEmail())).thenReturn(approver);

        // when
        List<RequestResponseDto> result = requestService.getAllRequestsToApprove(approver.getEmail());

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getStartDate()).isEqualTo(request2.getStartDate());
        assertThat(result.get(1).getEndDate()).isEqualTo(request1.getEndDate());
    }

}
