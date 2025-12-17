package eg.edu.alexu.cse.mail_server.Service.chain;

import eg.edu.alexu.cse.mail_server.dto.UserFormDto;
import eg.edu.alexu.cse.mail_server.dto.UserResponseDto;

public abstract class UserHandler{
    protected UserHandler nextHandler;

    public UserHandler setNext(UserHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler; // Return next so we can chain: A.setNext(B).setNext(C)
    }

    public abstract UserResponseDto handle(UserFormDto request);

    protected UserResponseDto handleNext(UserFormDto request) {
        if (nextHandler == null) {
            return null; // End of chain
        }
        return nextHandler.handle(request);
    }

}
