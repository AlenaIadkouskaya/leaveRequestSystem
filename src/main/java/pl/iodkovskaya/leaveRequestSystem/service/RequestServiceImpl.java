package pl.iodkovskaya.leaveRequestSystem.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.RequestRepository;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;

    @Override
    public void createLeaveRequest(String userEmail, RequestDto leaveRequestDto) {
        UserEntity userByEmail = userService.findUserByEmail(userEmail);
        if (userByEmail == null) {
            throw new EntityNotFoundException("User not found with email: " + userEmail);
        }
        LocalDate startVacation = leaveRequestDto.getStartDate();
        LocalDate endVacation = leaveRequestDto.getStartDate().plusDays(leaveRequestDto.getDurationVacation() + 1);


        RequestEntity newRequest = new RequestEntity(
                userByEmail,
                RequestStatus.CREATED,
                startVacation,
                endVacation);
        if (hasOverlappingRequests(newRequest)) {
            throw new IllegalArgumentException("There is already a leave request for this period.");
        }
        try {
            requestRepository.save(newRequest);
        } catch (Exception e) {
            throw new RuntimeException("Error saving leave request: " + e.getMessage(), e);
        }
    }

    private boolean hasOverlappingRequests(RequestEntity requestEntity) {

        return requestRepository.findAllByEmployeeAndDateRange(
                requestEntity.getEmployee(),
                requestEntity.getStartDate(),
                requestEntity.getEndDate()).size() > 0;
    }
}
