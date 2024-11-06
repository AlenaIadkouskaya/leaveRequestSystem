package pl.iodkovskaya.leaveRequestSystem.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.iodkovskaya.leaveRequestSystem.exception.StatusException;
//import pl.iodkovskaya.leaveRequestSystem.model.dto.ChangeStatusDto;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.RequestRepository;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final VacationBalanceService vacationBalanceService;

    @Override
    @Transactional
    public UUID createLeaveRequest(String userEmail, RequestDto leaveRequestDto) {
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

        vacationBalanceService.checkRemainderForUser(userByEmail, leaveRequestDto.getDurationVacation());

        if (hasOverlappingRequests(newRequest)) {
            throw new IllegalArgumentException("There is already a leave request for this period");
        }

        try {
            RequestEntity savedRequest = requestRepository.save(newRequest);
            vacationBalanceService.updateRemainder(userByEmail, leaveRequestDto.getDurationVacation());
            return savedRequest.getTechnicalId();
        } catch (Exception e) {
            throw new RuntimeException("Error saving leave request: " + e.getMessage(), e);
        }
    }

    private boolean hasOverlappingRequests(RequestEntity requestEntity) {

        return requestRepository.findAllByUserAndDateRange(
                requestEntity.getUser(),
                requestEntity.getStartDate(),
                requestEntity.getEndDate()).size() > 0;
    }

    @Override
    @Transactional
    public void approveRequest(String userEmail, UUID technicalId) throws AccessDeniedException {
        UserEntity approver = userService.findUserByEmail(userEmail);
        if (approver == null) {
            throw new EntityNotFoundException("User not found with email: " + userEmail);
        }

        RequestEntity request = requestRepository.findByTechnicalId(technicalId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with technical ID: " + technicalId));

        request.approve(approver);
//        if (isRejectedOrCancelled(request.getStatus()))
//            updateVacationBalance(approver, request);
    }

    private boolean isRejectedOrCancelled(RequestStatus status) {
        return status == RequestStatus.REJECTED;
    }

    private void updateVacationBalance(UserEntity user, RequestEntity request) {
        int days = (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) * -1;
        vacationBalanceService.updateRemainder(user, days);
    }
}
