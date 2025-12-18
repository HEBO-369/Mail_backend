package eg.edu.alexu.cse.mail_server.Service.command;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import eg.edu.alexu.cse.mail_server.Entity.Mail;
import eg.edu.alexu.cse.mail_server.Repository.MailRepository;
import eg.edu.alexu.cse.mail_server.Repository.UserRepository;
import eg.edu.alexu.cse.mail_server.dto.ComposeEmailDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DraftCommand {
    private final UserRepository userRepository;
    private final MailRepository mailRepository;


    @Transactional
    public Long execute(ComposeEmailDTO dto) {
        var senderUser = userRepository.findByEmail(dto.getSender())
                .orElseThrow(() -> new RuntimeException("Sender email not found: " + dto.getSender()));

        Mail draft = Mail.builder()
                .sender(dto.getSender())
                .senderRel(senderUser)
                .receiver(String.join(", ", dto.getReceivers()))
                .subject(dto.getSubject())
                .body(dto.getBody())
                .timestamp(LocalDateTime.now())
                .folderName("DRAFTS")
                .isRead(true)
                .owner(senderUser)  // Set owner for draft
                .build();

        Mail savedDraft = mailRepository.save(draft);
        return savedDraft.getMailId(); // Return the draft ID
    }
}
