package eg.edu.alexu.cse.mail_server.Controller;


import eg.edu.alexu.cse.mail_server.Service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/folder")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class FolderController {

    private final MailService mailService;

    /**
     * Copy an email to a custom folder
     *
     * @param mailId     ID of the email to copy
     * @param folderName Name of the target folder
     * @return Success message
     */
    @PostMapping("/copy")
    public Map<String, String> copyEmailToFolder(
            @RequestParam Long mailId,
            @RequestParam String folderName
    ) {
        mailService.copyEmailToFolder(mailId, folderName);
        return Map.of("message", "Email copied to folder: " + folderName);
    }

}
