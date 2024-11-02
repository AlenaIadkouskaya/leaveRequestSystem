package pl.iodkovskaya.leaveRequestSystem.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.iodkovskaya.leaveRequestSystem.exception.InvalidOperationException;
import pl.iodkovskaya.leaveRequestSystem.model.dto.VacationBalanceDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.vacationbalance.VacationBalanceEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.VacationBalanceRepository;

@Service
@AllArgsConstructor
public class VacationBalanceServiceImpl implements VacationBalanceService {
    private final UserService userService;
    private final VacationBalanceRepository vacationBalanceRepository;

    @Override
    public void addRecord(VacationBalanceDto vacationBalanceDto) {
        UserEntity userById = userService.findUserById(vacationBalanceDto.getEmployeeId());
        if (userById == null) {
            throw new EntityNotFoundException("User not found with ID: " + vacationBalanceDto.getEmployeeId());
        }
        VacationBalanceEntity vacationBalanceEntity = new VacationBalanceEntity(userById,
                vacationBalanceDto.getTotalDays(),
                vacationBalanceDto.getUsedDays());

        vacationBalanceRepository.save(vacationBalanceEntity);
    }

    @Override
    public void checkRemainderForUser(UserEntity user, Integer countDays) {
        VacationBalanceEntity vacationBalance = vacationBalanceRepository.findByUser(user);
        if (vacationBalance == null) {
            throw new InvalidOperationException("No data on remaining vacation days");
        }
        if (vacationBalance.getRemainingDays() < countDays) {
            throw new InvalidOperationException("Insufficient vacation days available. Your remainder: " + vacationBalance.getRemainingDays());
        }
    }

    @Override
    public void updateRemainder(UserEntity user, Integer durationVacation) {
        VacationBalanceEntity vacationBalance = vacationBalanceRepository.findByUser(user);
        vacationBalance.increaseUsedDays(durationVacation);
    }
}
