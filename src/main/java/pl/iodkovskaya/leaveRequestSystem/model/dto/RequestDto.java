package pl.iodkovskaya.leaveRequestSystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    //private Long employeeId;
    private String startDate;
    private String endDate;
}
