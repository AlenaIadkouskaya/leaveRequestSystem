package pl.iodkovskaya.leaveRequestSystem.service;

import org.springframework.stereotype.Service;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

@Service
public class RequestServiceImpl implements RequestService{
    @Override
    public void createLeaveRequest(UserEntity currentUser, RequestDto leaveRequestDto) {

//        RequestEntity newRequest = new RequestEntity(currentUser.RequestStatus.CREATED, leaveRequestDto.getStartDate(), leaveRequestDto.getEndDate());
//        return requestRepository.save(newRequest);
        System.out.println();
    }
}
