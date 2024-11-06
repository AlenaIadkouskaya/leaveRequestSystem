package pl.iodkovskaya.leaveRequestSystem.mapper;

import org.springframework.stereotype.Component;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestResponseDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;

@Component
public class RequestMapper {
    public RequestResponseDto fromEntity(RequestEntity requestEntity) {
        return new RequestResponseDto(requestEntity.getTechnicalId(),
                requestEntity.getUser().getFirstName() + " " + requestEntity.getUser().getLastName(),
                requestEntity.getStatus(),
                requestEntity.getStartDate(),
                requestEntity.getEndDate()
        );
    }
}
