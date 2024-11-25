package pl.iodkovskaya.leaveRequestSystem.service;

//import pl.iodkovskaya.leaveRequestSystem.model.dto.ChangeStatusDto;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestResponseDto;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

public interface RequestService {
    UUID createLeaveRequest(String userEmail, RequestDto leaveRequestDto);
    void approveRequest(String userEmail, UUID technicalId) throws AccessDeniedException;
    void rejectRequest(String userEmail, UUID technicalId) throws AccessDeniedException;
    List<RequestResponseDto> getAllRequests();
    RequestResponseDto getRequestById(UUID id);
    List<RequestResponseDto> getRequestsByUser(String userEmail);
    List<RequestResponseDto> getAllRequestsToApprove(String username);
    void approveRequestAsync(String userEmail, UUID technicalId);
}
