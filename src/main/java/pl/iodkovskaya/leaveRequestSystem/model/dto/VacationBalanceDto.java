package pl.iodkovskaya.leaveRequestSystem.model.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VacationBalanceDto {
    @NotNull(message = "Employee ID cannot be null")
    private Long employeeId;

    @NotNull(message = "Year cannot be null.")
    @Min(value = 2000, message = "Year must be greater than or equal to 2000")
    private Integer year;

    @NotNull(message = "Total days cannot be null.")
    @Min(value = 0, message = "Total days must be greater than or equal to 0")
    private Integer totalDays;

    private Integer usedDays;

    public VacationBalanceDto(Long employeeId, Integer year, Integer totalDays, Integer usedDays) {
        this.employeeId = employeeId;
        this.year = year;
        this.totalDays = totalDays;
        this.usedDays = (usedDays != null) ? usedDays : 0;
    }
}
