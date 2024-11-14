package pl.iodkovskaya.leaveRequestSystem.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.iodkovskaya.leaveRequestSystem.model.dto.VacationBalanceDto;
import pl.iodkovskaya.leaveRequestSystem.service.VacationBalanceService;

@AllArgsConstructor
@RestController
@RequestMapping("/api/vacation-balance")
public class VacationBalanceController {
    private final VacationBalanceService vacationBalanceService;
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ACCOUNTANT')")
    @PostMapping("/new")
    public ResponseEntity<String> createVacationBalance(@Valid @RequestBody VacationBalanceDto vacationBalanceDto) {
        vacationBalanceService.addRecord(vacationBalanceDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("The data regarding the remaining days has been successfully added");
    }
}
