package pl.iodkovskaya.leaveRequestSystem.service;

import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

import java.util.UUID;

public interface RequestService {
    UUID createLeaveRequest(String userEmail, RequestDto leaveRequestDto);
}
