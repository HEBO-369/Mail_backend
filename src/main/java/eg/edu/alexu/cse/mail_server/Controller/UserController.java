package eg.edu.alexu.cse.mail_server.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eg.edu.alexu.cse.mail_server.Service.UserService;
import eg.edu.alexu.cse.mail_server.dto.UserFormDto;
import eg.edu.alexu.cse.mail_server.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public UserResponseDto register(@RequestBody UserFormDto form){
        return userService.register(form);
    }

    @PostMapping("/login")
    public UserResponseDto login(@RequestBody UserFormDto form){
        return userService.login(form);
    }
}
