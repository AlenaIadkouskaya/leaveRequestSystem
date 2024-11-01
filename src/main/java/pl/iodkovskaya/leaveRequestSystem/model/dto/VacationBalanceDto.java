package pl.iodkovskaya.leaveRequestSystem.model.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VacationBalanceDto {
    @NotNull(message = "Employee ID cannot be null.")
    private Long employeeId;

    @NotNull(message = "Year cannot be null.")
    @Min(value = 2000, message = "Year must be greater than or equal to 2000.")
    private Integer year;

    @NotNull(message = "Total days cannot be null.")
    @Min(value = 0, message = "Total days must be greater than or equal to 0.")
    private Integer totalDays;

    @Min(value = 0, message = "Used days must be greater than or equal to 0.")
    private Integer usedDays;
}
