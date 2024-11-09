package pl.iodkovskaya.leaveRequestSystem.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.iodkovskaya.leaveRequestSystem.exception.InvalidOperationException;
import pl.iodkovskaya.leaveRequestSystem.model.dto.VacationBalanceDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.vacationbalance.VacationBalanceEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.VacationBalanceRepository;

import java.time.temporal.ChronoUnit;

@Service
@AllArgsConstructor
public class VacationBalanceServiceImpl implements VacationBalanceService, RequestListener {
    private final UserService userService;
    private final VacationBalanceRepository vacationBalanceRepository;

    @Override
    public void addRecord(VacationBalanceDto vacationBalanceDto) {
        UserEntity userById = userService.findUserById(vacationBalanceDto.getUserId());
        if (userById == null) {
            throw new EntityNotFoundException("User not found with ID: " + vacationBalanceDto.getUserId());
        }
        VacationBalanceEntity vacationBalanceEntity = new VacationBalanceEntity(userById,
                vacationBalanceDto.getTotalDays(),
                vacationBalanceDto.getUsedDays());

        vacationBalanceRepository.save(vacationBalanceEntity);
    }

    @Override
    public void checkRemainderForUser(UserEntity user, Integer countDays) {
        VacationBalanceEntity vacationBalance = vacationBalanceRepository.findByUser(user)
                .orElseThrow(() -> new InvalidOperationException("No data on remaining vacation days"));
        if (vacationBalance.getRemainingDays() < countDays) {
            throw new InvalidOperationException("Insufficient vacation days available. Your remainder: " + vacationBalance.getRemainingDays());
        }
    }

    @Override
    public void decreaseRemainder(UserEntity user, RequestEntity request) {
        int days = getDaysVacation(request);
        VacationBalanceEntity vacationBalance = vacationBalanceRepository.findByUser(user)
                .orElseThrow(() -> new InvalidOperationException("No data on remaining vacation days"));
        vacationBalance.increaseUsedDays(days);
    }

    @Override
    public void increaseRemainder(UserEntity user, RequestEntity request) {
        int days = getDaysVacation(request);
        VacationBalanceEntity vacationBalance = vacationBalanceRepository.findByUser(user)
                .orElseThrow(() -> new InvalidOperationException("No data on remaining vacation days"));
        vacationBalance.decreaseUsedDays(days);
    }
    private static int getDaysVacation(RequestEntity request) {
        int days = (int) (ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1);
        return days;
    }
}
