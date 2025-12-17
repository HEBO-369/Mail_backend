package eg.edu.alexu.cse.mail_server.Service.command;

import eg.edu.alexu.cse.mail_server.Entity.Mail;
import eg.edu.alexu.cse.mail_server.Repository.MailRepository;
import eg.edu.alexu.cse.mail_server.Service.AttachmentService;
import eg.edu.alexu.cse.mail_server.dto.AttachmentDTO;
import eg.edu.alexu.cse.mail_server.dto.EmailViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;


@Component
@RequiredArgsConstructor
public class GetMailCommand {
    private final MailRepository mailRepository;
    private final AttachmentService attachmentService;

    /**
     * Execute the command to get mail with attachments.
     *
     * @param mailId the ID of the mail to retrieve
     * @return EmailViewDto containing mail data and attachments with Base64-encoded file data
     * @throws RuntimeException if mail is not found
     * @throws IOException if file reading fails
     */
    public EmailViewDto execute(Long mailId) throws IOException {

        Mail mail = mailRepository.findById(mailId)
                .orElseThrow(() -> new RuntimeException("Mail not found with id: " + mailId));


        List<AttachmentDTO> attachments = attachmentService.getAttachmentsWithData(mailId);

        return EmailViewDto.builder()
                .id(mail.getMailId())
                .sender(mail.getSender())
                .subject(mail.getSubject())
                .body(mail.getBody())
                .timestamp(mail.getTimestamp())
                .priority(mail.getPriority())
                .isRead(mail.isRead())
                .attachments(attachments)
                .build();
    }
}

