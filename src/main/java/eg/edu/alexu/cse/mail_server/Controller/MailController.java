package eg.edu.alexu.cse.mail_server.Controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import eg.edu.alexu.cse.mail_server.Entity.Mail;
import eg.edu.alexu.cse.mail_server.Service.MailService;
import eg.edu.alexu.cse.mail_server.dto.ComposeEmailDTO;
import eg.edu.alexu.cse.mail_server.dto.EmailViewDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mail")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @PostMapping("/send-with-attachments")
    public Map<String, String> sendMail(
            @RequestPart("email") ComposeEmailDTO composeEmailDTO,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) throws IOException {
        mailService.sendWithAttachments(composeEmailDTO, attachments);
        return Map.of("message", "Email sent successfully");
    }

    @PostMapping("/draft")
    public Map<String, String> draftEmail(@RequestBody ComposeEmailDTO composeEmailDTO) {
        mailService.draft(composeEmailDTO);
        return Map.of("message", "Email drafted successfully");
    }

    // Get inbox emails
    @GetMapping("/inbox/{userEmail}")
    public List<EmailViewDto> getInboxMails(@PathVariable String userEmail) {
        return mailService.getInboxMails(userEmail);
    }

    // Get sent emails
    @GetMapping("/sent/{userEmail}")
    public List<EmailViewDto> getSentMails(@PathVariable String userEmail) {
        return mailService.getSentMails(userEmail);
    }
    
    // Get draft emails
    @GetMapping("/drafts/{userEmail}")
    public List<EmailViewDto> getDraftMails(@PathVariable String userEmail) {
        return mailService.getDraftMails(userEmail);
    }

    // Get trash emails
    @GetMapping("/trash/{userEmail}")
    public List<EmailViewDto> getTrashMails(@PathVariable String userEmail) {
        return mailService.getTrashMails(userEmail);
    }

    // Get emails by folder
    @GetMapping("/folder/{userEmail}/{folderName}")
    public List<EmailViewDto> getMailsByFolder(@PathVariable String userEmail, @PathVariable String folderName) {
        return mailService.getMailsByFolder(userEmail, folderName);
    }

    // Get mail by ID
    @GetMapping("/{mailId}")
    public Mail getMailById(@PathVariable Long mailId) {
        return mailService.getMailById(mailId);
    }

    @GetMapping("/sortMail/{email}/{criteria}/{order}")
    public List<Mail> getSortedMails(@PathVariable String email, @PathVariable String criteria, @PathVariable boolean order){
        return mailService.getSortedMails(email, criteria, order);
    }

    // Mark as read
    @PutMapping("/{mailId}/read")
    public Map<String, String> markAsRead(@PathVariable Long mailId) {
        mailService.markAsRead(mailId);
        return Map.of("message", "Mail marked as read");
    }

    // Mark as unread
    @PutMapping("/{mailId}/unread")
    public Map<String, String> markAsUnread(@PathVariable Long mailId) {
        mailService.markAsUnread(mailId);
        return Map.of("message", "Mail marked as unread");
    }

    // Delete mail (move to trash)
    @DeleteMapping("/{mailId}")
    public Map<String, String> deleteMail(@PathVariable Long mailId) {
        mailService.deleteMail(mailId);
        return Map.of("message", "Mail deleted successfully");
    }

}
