package pl.iodkovskaya.leaveRequestSystem.service;

import org.springframework.web.bind.annotation.RequestBody;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestDto;
import pl.iodkovskaya.leaveRequestSystem.model.dto.VacationBalanceDto;

public interface VacationBalanceService {
    void addRecord(VacationBalanceDto vacationBalanceDto);
}
