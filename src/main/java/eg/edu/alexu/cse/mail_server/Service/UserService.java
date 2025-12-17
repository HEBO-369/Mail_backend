package eg.edu.alexu.cse.mail_server.Service;

import eg.edu.alexu.cse.mail_server.Entity.User;
import eg.edu.alexu.cse.mail_server.Repository.UserRepository;
import eg.edu.alexu.cse.mail_server.Service.chain.SaveUserHandler;
import eg.edu.alexu.cse.mail_server.Service.chain.UserExistenceHandler;
import eg.edu.alexu.cse.mail_server.Service.chain.UserHandler;
import eg.edu.alexu.cse.mail_server.Service.chain.ValidateUserHandler;
import eg.edu.alexu.cse.mail_server.dto.UserFormDto;
import eg.edu.alexu.cse.mail_server.dto.UserResponseDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ValidateUserHandler validateHandler;
    private final UserExistenceHandler existenceHandler;
    private final SaveUserHandler saveHandler;

    private UserHandler registrationChain;

    @PostConstruct
    public void init() {
        registrationChain = validateHandler;
        registrationChain.setNext(existenceHandler).setNext(saveHandler);
    }

    public UserResponseDto register(UserFormDto form) {
        return registrationChain.handle(form);
    }

    public UserResponseDto login(UserFormDto form) {
        Optional<User> userOptional = userRepository.findByEmail(form.getEmail());
        if (userOptional.isPresent() && userOptional.get().getPassword().equals(form.getPassword())) {
            User user = userOptional.get();
            return UserResponseDto.builder()
                    .id(user.getUserId())
                    .name(user.getFirstName() + " " + user.getLastName())
                    .email(user.getEmail())
                    .build();
        }
        throw new RuntimeException("Incorrect password or invalid email address");
    }

}
