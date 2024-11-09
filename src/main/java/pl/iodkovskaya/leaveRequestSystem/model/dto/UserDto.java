package pl.iodkovskaya.leaveRequestSystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String login;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
}
