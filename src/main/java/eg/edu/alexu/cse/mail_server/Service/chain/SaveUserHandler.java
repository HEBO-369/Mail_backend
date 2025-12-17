package eg.edu.alexu.cse.mail_server.Service.chain;

import eg.edu.alexu.cse.mail_server.Entity.User;
import eg.edu.alexu.cse.mail_server.Repository.UserRepository;
import eg.edu.alexu.cse.mail_server.dto.UserFormDto;
import eg.edu.alexu.cse.mail_server.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaveUserHandler extends UserHandler {
    private final UserRepository userRepository;

    @Override
    public UserResponseDto handle(UserFormDto request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        User savedUser = userRepository.save(user);

        System.out.println("Step 3: User Saved to Database.");

        return UserResponseDto.builder()
                .id(savedUser.getUserId())
                .name(savedUser.getFirstName() + " " + savedUser.getLastName())
                .email(savedUser.getEmail())
                .build();
    }
}
