package eg.edu.alexu.cse.mail_server.Controller;


import eg.edu.alexu.cse.mail_server.Service.FilterService;
import eg.edu.alexu.cse.mail_server.dto.EmailViewDto;
import eg.edu.alexu.cse.mail_server.dto.MailFilterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filter")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class FilterController {
    private final FilterService filterService;

    /**
     * Filter emails using AND logic - all criteria must match
     * Only returns emails for the specified user
     * @param userId the ID of the user performing the search
     * @param mailFilterDTO the filter criteria
     * @return list of filtered emails for this user
     */
    @PostMapping("/{userId}/and")
    public List<EmailViewDto> getEmailsAnd(
            @PathVariable Long userId,
            @RequestBody MailFilterDTO mailFilterDTO) {
        mailFilterDTO.setUserId(userId);
        return filterService.getEmailsAnd(mailFilterDTO);
    }

    /**
     * Filter emails using OR logic - at least one criterion must match
     * Only returns emails for the specified user
     * @param userId the ID of the user performing the search
     * @param mailFilterDTO the filter criteria
     * @return list of filtered emails for this user
     */
    @PostMapping("/{userId}/or")
    public List<EmailViewDto> getEmailsOr(
            @PathVariable Long userId,
            @RequestBody MailFilterDTO mailFilterDTO) {
        mailFilterDTO.setUserId(userId);
        return filterService.getEmailsOr(mailFilterDTO);
    }

    /**
     * Search emails (default: OR logic)
     * Only returns emails for the specified user
     * @param userId the ID of the user performing the search
     * @param mailFilterDTO the search criteria
     * @return list of matching emails for this user
     */
    @PostMapping("/{userId}/search")
    public List<EmailViewDto> searchEmails(
            @PathVariable Long userId,
            @RequestBody MailFilterDTO mailFilterDTO) {
        mailFilterDTO.setUserId(userId);
        return filterService.getEmailsOr(mailFilterDTO);
    }
}
