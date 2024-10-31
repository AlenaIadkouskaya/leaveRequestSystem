package pl.iodkovskaya.leaveRequestSystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.ErrorCode;

@AllArgsConstructor
@Getter
public class MessageResponse {
    private String message;
    private ErrorCode errorCode;

    public MessageResponse(String message) {
        this.message = message;
    }
}
