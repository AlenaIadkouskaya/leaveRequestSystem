package pl.iodkovskaya.leaveRequestSystem.service;

import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

public interface RequestService {
    void createLeaveRequest(String userEmail, RequestDto leaveRequestDto);
}
