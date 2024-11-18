package pl.iodkovskaya.leaveRequestSystem.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class VacationBalanceDto {
    @NotNull(message = "Employee ID cannot be null")
    private Long userId;

    @NotNull(message = "Total days cannot be null.")
    @Min(value = 0, message = "Total days must be greater than or equal to 0")
    private Integer totalDays;

    private Integer usedDays;

    @NotNull(message = "Hire date cannot be null")
    private LocalDate hireDate;

    public VacationBalanceDto(Long userId, Integer totalDays, Integer usedDays, LocalDate hireDate) {
        this.userId = userId;
        this.totalDays = totalDays;
        this.usedDays = (usedDays != null) ? usedDays : 0;
        this.hireDate = hireDate;
    }
}
