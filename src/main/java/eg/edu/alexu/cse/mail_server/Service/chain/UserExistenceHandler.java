package eg.edu.alexu.cse.mail_server.Service.chain;

import eg.edu.alexu.cse.mail_server.Repository.UserRepository;
import eg.edu.alexu.cse.mail_server.dto.UserFormDto;
import eg.edu.alexu.cse.mail_server.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserExistenceHandler extends UserHandler{

    private final UserRepository userRepository;
    @Override
    public UserResponseDto handle(UserFormDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }
        System.out.println("Step 2: User does not exist. Proceeding.");
        return handleNext(request);
    }
}
