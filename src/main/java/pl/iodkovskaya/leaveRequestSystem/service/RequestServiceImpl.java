package pl.iodkovskaya.leaveRequestSystem.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.iodkovskaya.leaveRequestSystem.mapper.RequestMapper;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestResponseDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.RequestRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final VacationBalanceService vacationBalanceService;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public UUID createLeaveRequest(String userEmail, RequestDto leaveRequestDto) {
        UserEntity userByEmail = userService.findUserByEmail(userEmail);
        if (userByEmail == null) {
            throw new EntityNotFoundException("User not found with email: " + userEmail);
        }
        LocalDate startVacation = leaveRequestDto.getStartDate();
        LocalDate endVacation = leaveRequestDto.getStartDate().plusDays(leaveRequestDto.getDurationVacation() - 1);


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

    @Override
    @Transactional
    public void approveRequest(String userEmail, UUID technicalId) {
        UserEntity approver = userService.findUserByEmail(userEmail);
        if (approver == null) {
            throw new EntityNotFoundException("User not found with email: " + userEmail);
        }

        RequestEntity request = requestRepository.findByTechnicalId(technicalId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with technical ID: " + technicalId));

        request.approve(approver);
    }

    @Override
    @Transactional
    public void rejectRequest(String userEmail, UUID technicalId) {
        RequestEntity request = requestRepository.findByTechnicalId(technicalId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with technical ID: " + technicalId));

        request.reject();
        updateVacationBalance(request.getUser(), request);
    }

    @Override
    public List<RequestResponseDto> getAllRequests() {
        return requestRepository.findAll().stream().map(e -> requestMapper.fromEntity(e)).toList();
    }

    private boolean hasOverlappingRequests(RequestEntity requestEntity) {

        return requestRepository.findAllByUserAndDateRange(
                requestEntity.getUser(),
                requestEntity.getStartDate(),
                requestEntity.getEndDate()).size() > 0;
    }

    private void updateVacationBalance(UserEntity user, RequestEntity request) {
        int days = (int) (ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1) * (-1);
        vacationBalanceService.updateRemainder(user, days);
    }

    @Override
    public RequestResponseDto getRequestById(UUID id) {
        RequestEntity request = requestRepository.findByTechnicalId(id)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with technical ID: " + id));
        return requestMapper.fromEntity(request);
    }
}
