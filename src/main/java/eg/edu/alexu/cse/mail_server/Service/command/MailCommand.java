package eg.edu.alexu.cse.mail_server.Service.command;

import eg.edu.alexu.cse.mail_server.dto.ComposeEmailDTO;

public interface MailCommand {
    void execute(ComposeEmailDTO dto);
}
