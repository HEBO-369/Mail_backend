package eg.edu.alexu.cse.mail_server.Service.command;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import eg.edu.alexu.cse.mail_server.Entity.Attachment;
import eg.edu.alexu.cse.mail_server.Entity.Mail;
import eg.edu.alexu.cse.mail_server.Entity.User;
import eg.edu.alexu.cse.mail_server.Repository.MailRepository;
import eg.edu.alexu.cse.mail_server.Repository.UserRepository;
import eg.edu.alexu.cse.mail_server.Service.AttachmentService;
import eg.edu.alexu.cse.mail_server.dto.ComposeEmailDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SendCommand implements MailCommand {
    private final UserRepository userRepository;
    private final MailRepository mailRepository;
    private final AttachmentService attachmentService;

    @Override
    @Transactional   // if error in receiver copy consider not sent copy (all or nothing)
    public void execute(ComposeEmailDTO dto) {
        User senderUser = userRepository.findByEmail(dto.getSender())
                .orElseThrow(() -> new RuntimeException("Sender email not found: " + dto.getSender()));

        Mail sentCopy = Mail.builder()
                .sender(dto.getSender())
                .senderRel(senderUser)
                .receiver(String.join(",", dto.getReceivers()))
                .body(dto.getBody())
                .subject(dto.getSubject())
                .priority(dto.getPriority())
                .attachments(null) // still need to handle
                .folderName("SENT")
                .isRead(true)
                .timestamp(LocalDateTime.now())
                .owner(senderUser)  // Set owner for sent copy
                .build();

        mailRepository.save(sentCopy);

        Queue<String> receiverQueue = new LinkedList<>(dto.getReceivers());
        List<String> failedReceivers = new ArrayList<>();

        while (!receiverQueue.isEmpty()) {
            String receiverEmail = receiverQueue.poll();

            if (userRepository.findByEmail(receiverEmail).isPresent()) {
                User receiverUser = userRepository.findByEmail(receiverEmail).get();
                Mail inboxCopy = Mail.builder()
                        .sender(dto.getSender())
                        .senderRel(senderUser)
                        .receiver(receiverEmail)
                        .subject(dto.getSubject())
                        .body(dto.getBody())
                        .priority(dto.getPriority())
                        .timestamp(LocalDateTime.now())
                        .folderName("INBOX")
                        .isRead(false)
                        .owner(receiverUser)  // Set owner as receiver for inbox copy
                        .build();

                mailRepository.save(inboxCopy);
            } else {
                failedReceivers.add(receiverEmail);
            }
        }
        if (!failedReceivers.isEmpty()) {
            throw new RuntimeException("the following receivers were not found: " + String.join(",", failedReceivers));
        }
    }

    /**
     * Execute send command with attachments
     * Creates sent copy with attachments and inbox copies for all receivers
     * @param dto email details
     * @param attachments list of files to attach
     * @throws IOException if file processing fails
     */
    @Transactional
    public void executeWithAttachments(ComposeEmailDTO dto, List<MultipartFile> attachments) throws IOException {
        User senderUser = userRepository.findByEmail(dto.getSender())
                .orElseThrow(() -> new RuntimeException("Sender email not found: " + dto.getSender()));

        // Create sent copy
        Mail sentCopy = Mail.builder()
                .sender(dto.getSender())
                .senderRel(senderUser)
                .receiver(String.join(",", dto.getReceivers()))
                .body(dto.getBody())
                .subject(dto.getSubject())
                .priority(dto.getPriority())
                .folderName("SENT")
                .isRead(true)
                .timestamp(LocalDateTime.now())
                .owner(senderUser)  // Set owner for sent copy
                .build();

        mailRepository.save(sentCopy);

        // Save attachments for sent copy
        if (attachments != null && !attachments.isEmpty()) {
            for (MultipartFile file : attachments) {
                attachmentService.saveAttachment(file, sentCopy);
            }
        }

        // Send to all receivers
        Queue<String> receiverQueue = new LinkedList<>(dto.getReceivers());
        List<String> failedReceivers = new ArrayList<>();

        while (!receiverQueue.isEmpty()) {
            String receiverEmail = receiverQueue.poll();

            if (userRepository.findByEmail(receiverEmail).isPresent()) {
                User receiverUser = userRepository.findByEmail(receiverEmail).get();
                Mail inboxCopy = Mail.builder()
                        .sender(dto.getSender())
                        .senderRel(senderUser)
                        .receiver(receiverEmail)
                        .subject(dto.getSubject())
                        .body(dto.getBody())
                        .priority(dto.getPriority())
                        .timestamp(LocalDateTime.now())
                        .folderName("INBOX")
                        .isRead(false)
                        .owner(receiverUser)  // Set owner as receiver for inbox copy
                        .build();

                mailRepository.save(inboxCopy);

                // Copy attachments for each receiver's inbox copy
                if (attachments != null && !attachments.isEmpty()) {
                    for (MultipartFile file : attachments) {
                        attachmentService.saveAttachment(file, inboxCopy);
                    }
                }
            } else {
                failedReceivers.add(receiverEmail);
            }
        }

        if (!failedReceivers.isEmpty()) {
            throw new RuntimeException("the following receivers were not found: " + String.join(",", failedReceivers));
        }
    }
}