package pl.iodkovskaya.leaveRequestSystem.service;

import pl.iodkovskaya.leaveRequestSystem.model.dto.VacationBalanceDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

public interface VacationBalanceService {
    void addRecord(VacationBalanceDto vacationBalanceDto);

    void checkRemainderForUser(UserEntity user, Integer countDays);

    void updateRemainder(UserEntity user, Integer durationVacation);
}
