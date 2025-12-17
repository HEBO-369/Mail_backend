package eg.edu.alexu.cse.mail_server.Service.chain;

import eg.edu.alexu.cse.mail_server.dto.UserFormDto;
import eg.edu.alexu.cse.mail_server.dto.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
public class ValidateUserHandler extends UserHandler{
    @Override
    public UserResponseDto handle(UserFormDto request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()){
            throw new RuntimeException("Email is required");
        }

        if (request.getPassword() == null || request.getPassword().length() < 3) {
            throw new RuntimeException("Password must be at least 3 characters");
        }

        System.out.println("Step 1: Validation Passed.");
        return handleNext(request);
    }
}
