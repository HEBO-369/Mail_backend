package eg.edu.alexu.cse.mail_server.dto;

import lombok.Data;

@Data
public class UserFormDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
