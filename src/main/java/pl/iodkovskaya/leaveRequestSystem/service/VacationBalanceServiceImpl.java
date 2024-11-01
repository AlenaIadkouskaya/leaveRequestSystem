package pl.iodkovskaya.leaveRequestSystem.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.iodkovskaya.leaveRequestSystem.model.dto.VacationBalanceDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.vacationbalance.VacationBalanceEntity;

@Service
@AllArgsConstructor
public class VacationBalanceServiceImpl implements VacationBalanceService{
    private final UserService userService;

    @Override
    public void addRecord(VacationBalanceDto vacationBalanceDto) {
        UserEntity userById = userService.findUserById(vacationBalanceDto.getEmployeeId());
        if (userById == null) {
            throw new EntityNotFoundException("User not found with ID: " + vacationBalanceDto.getEmployeeId());
        }
        new VacationBalanceEntity(userById,
                vacationBalanceDto.getYear(),
                vacationBalanceDto.getTotalDays(),
                vacationBalanceDto.getUsedDays());
    }
}
