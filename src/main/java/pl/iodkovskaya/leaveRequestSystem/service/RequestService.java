package pl.iodkovskaya.leaveRequestSystem.service;

//import pl.iodkovskaya.leaveRequestSystem.model.dto.ChangeStatusDto;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

public interface RequestService {
    UUID createLeaveRequest(String userEmail, RequestDto leaveRequestDto);
    void approveRequest(String userEmail, UUID technicalId) throws AccessDeniedException;
}
