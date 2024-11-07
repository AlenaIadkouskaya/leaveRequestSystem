package pl.iodkovskaya.leaveRequestSystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;

import java.time.LocalDate;
import java.util.UUID;
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestResponseDto {
    private UUID technicalId;
    private String user;
    private RequestStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
}
