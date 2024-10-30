package pl.iodkovskaya.leaveRequestSystem.model.dto;

import lombok.Data;

@Data
public class UserDto {
    private String login;
    private String password;
    private String email;
}
