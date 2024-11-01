package pl.iodkovskaya.leaveRequestSystem.model.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    @NotNull(message = "Start date must not be null.")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @NotNull(message = "Duration of vacation must not be null.")
    @Positive(message = "Duration of vacation must be greater than 0.")
    private Integer durationVacation;
}
