package eg.edu.alexu.cse.mail_server.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import eg.edu.alexu.cse.mail_server.Entity.Attachment;
import eg.edu.alexu.cse.mail_server.Entity.Mail;
import eg.edu.alexu.cse.mail_server.Repository.MailRepository;
import eg.edu.alexu.cse.mail_server.Service.command.DraftCommand;
import eg.edu.alexu.cse.mail_server.Service.command.GetMailCommand;
import eg.edu.alexu.cse.mail_server.Service.command.SendCommand;
import eg.edu.alexu.cse.mail_server.dto.AttachmentDTO;
import eg.edu.alexu.cse.mail_server.dto.ComposeEmailDTO;
import eg.edu.alexu.cse.mail_server.dto.EmailViewDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {
    private final SendCommand sendCommand;
    private final DraftCommand draftCommand;
    private final GetMailCommand getMailCommand;
    private final MailRepository mailRepository;
    private final AttachmentService attachmentService;
    private final eg.edu.alexu.cse.mail_server.Repository.UserRepository userRepository;

    public void send(ComposeEmailDTO composeEmailDTO) {
        sendCommand.execute(composeEmailDTO);
    }

    /**
     * Send email with attachments
     * @param composeEmailDTO email details
     * @param attachments list of files to attach
     * @throws IOException if file processing fails
     */
    public void sendWithAttachments(ComposeEmailDTO composeEmailDTO, List<MultipartFile> attachments) throws IOException {
        sendCommand.executeWithAttachments(composeEmailDTO, attachments);
    }

    public Long draft(ComposeEmailDTO composeEmailDTO) {
        return draftCommand.execute(composeEmailDTO);
    }

    // Get inbox mails
    public List<EmailViewDto> getInboxMails(String userEmail) {
        Long userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getUserId();
        List<Mail> mails = mailRepository.findByOwnerIdAndFolderNameOrderByTimestampDesc(userId, "INBOX");
        return mails.stream().map(this::convertToEmailViewDto).collect(Collectors.toList());
    }

    // Get sent mails
    public List<EmailViewDto> getSentMails(String userEmail) {
        Long userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getUserId();
        List<Mail> mails = mailRepository.findByOwnerIdAndFolderNameOrderByTimestampDesc(userId, "SENT");
        return mails.stream().map(this::convertToEmailViewDto).collect(Collectors.toList());
    }

    // Get draft mails
    public List<EmailViewDto> getDraftMails(String userEmail) {
        Long userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getUserId();
        List<Mail> mails = mailRepository.findByOwnerIdAndFolderNameOrderByTimestampDesc(userId, "DRAFTS");
        return mails.stream().map(this::convertToEmailViewDto).collect(Collectors.toList());
    }

    // Get trash mails
    public List<EmailViewDto> getTrashMails(String userEmail) {
        Long userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getUserId();
        List<Mail> mails = mailRepository.findByOwnerIdAndFolderNameOrderByTimestampDesc(userId, "trash");
        return mails.stream().map(this::convertToEmailViewDto).collect(Collectors.toList());
    }

    // Get mails by folder
    public List<EmailViewDto> getMailsByFolder(String userEmail, String folderName) {
        Long userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getUserId();

        List<Mail> mails;
        if ("all".equalsIgnoreCase(folderName)) {
            // All Mail shows only inbox and sent (excludes drafts and trash)
            mails = mailRepository.findAllByOwnerIdExcludingDraftsAndTrash(userId);
        } else {
            // Use ownerId to load mails for the given folder (including "trash")
            mails = mailRepository.findByOwnerIdAndFolderNameOrderByTimestampDesc(userId, folderName);
        }
        return mails.stream().map(this::convertToEmailViewDto).collect(Collectors.toList());
    }

    // Get mail by ID
    public Mail getMailById(Long mailId) {
        return mailRepository.findById(mailId)
                .orElseThrow(() -> new RuntimeException("Mail not found with id: " + mailId));
    }

    // Mark as read
    public void markAsRead(Long mailId) {
        Mail mail = getMailById(mailId);
        mail.setRead(true);
        mailRepository.save(mail);
    }

    public void markAsUnread(Long mailId) {
        Mail mail = getMailById(mailId);
        mail.setRead(false);
        mailRepository.save(mail);
    }

    // Delete mail (soft delete - move to trash)
    // Note: This requires userId parameter for ownership verification
    public void deleteMail(Long mailId, Long userId) {
        Mail mail = mailRepository.findByMailIdAndOwnerId(mailId, userId);
        if (mail == null) {
            throw new IllegalArgumentException("Mail not found or you don't have permission to delete it");
        }
        mail.setFolderName("trash");
        mail.setDeletedAt(java.time.LocalDateTime.now()); // Track when moved to trash
        mailRepository.save(mail);
    }

    // Overload for backward compatibility (when userId is not available)
    public void deleteMail(Long mailId) {
        Mail mail = getMailById(mailId);
        mail.setFolderName("trash");
        mail.setDeletedAt(java.time.LocalDateTime.now());
        mailRepository.save(mail);
    }

    /**
     * Permanently delete an email from database (hard delete)
     * This completely removes the email and cannot be undone
     * @param mailId ID of the email to permanently delete
     */
    public void permanentDeleteMail(Long mailId) {
        Mail mail = getMailById(mailId);
        mailRepository.delete(mail);
    }

    /**
     * Permanently delete emails that have been in trash for more than 1 minute
     * Called by scheduled task
     * TO CHANGE AUTO-DELETE TIME: Change .minusMinutes(1) to:
     * - .minusMinutes(5) for 5 minutes
     * - .minusHours(1) for 1 hour
     * - .minusDays(7) for 7 days
     * - .minusDays(30) for 30 days
     */
    public void deleteOldTrashEmails() {
        java.time.LocalDateTime oneMinuteAgo = java.time.LocalDateTime.now().minusMinutes(1);
        List<Mail> oldTrashMails = mailRepository.findByFolderNameAndDeletedAtBefore("trash", oneMinuteAgo);

        if (!oldTrashMails.isEmpty()) {
            mailRepository.deleteAll(oldTrashMails);
            System.out.println("Auto-deleted " + oldTrashMails.size() + " emails from trash (older than 1 minute)");
        }
    }

    /**
     * Copy an email to a custom folder
     * Creates a duplicate of the email with the specified folder name
     * @param mailId ID of the email to copy
     * @param folderName Name of the target folder
     */
    public void copyEmailToFolder(Long mailId, String folderName) {
        // Get original email
        Mail originalMail = getMailById(mailId);

        // Validate folder name
        if (folderName == null || folderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Folder name cannot be empty");
        }

        // Create a copy of the email (DO NOT copy mailId - let DB generate new one)
        Mail copiedMail = Mail.builder()
                // mailId is NOT set - database will auto-generate
                .sender(originalMail.getSender())
                .senderRel(originalMail.getSenderRel()) // Same user reference (not a collection)
                .receiver(originalMail.getReceiver())
                .subject(originalMail.getSubject())
                .body(originalMail.getBody())
                .priority(originalMail.getPriority())
                .timestamp(java.time.LocalDateTime.now()) // New timestamp for the copy
                .folderName(folderName.toUpperCase()) // Store folder name in uppercase
                .isRead(originalMail.isRead())
                .owner(originalMail.getOwner()) // Preserve owner
                .build();

        // Create NEW collection instances to avoid Hibernate shared reference error
        if (originalMail.getReceiverRel() != null) {
            copiedMail.setReceiverRel(new ArrayList<>(originalMail.getReceiverRel()));
        }

        if (originalMail.getAttachments() != null) {
            copiedMail.setAttachments(new ArrayList<>(originalMail.getAttachments()));
        }

        // Save the mail
        mailRepository.save(copiedMail);
    }

    /**
     * Get mail with all attachments including file data
     *
     * @param mailId the ID of the mail
     * @return EmailViewDto with attachments containing file data as Base64
     * @throws IOException if file reading fails
     */
    
    public EmailViewDto getMailWithAttachments(Long mailId) throws IOException {
        return getMailCommand.execute(mailId);
    }

    /**
     * Convert Mail entity to EmailViewDto with FULL attachment data (Base64 encoded)
     * Loads all attachment files and encodes them for frontend
     */
    private EmailViewDto convertToEmailViewDto(Mail mail) {
        // Load full attachment data with Base64 encoding
        List<AttachmentDTO> attachmentDTOs = null;
        if (mail.getAttachments() != null && !mail.getAttachments().isEmpty()) {
            attachmentDTOs = new ArrayList<>();
            for (Attachment attachment : mail.getAttachments()) {
                try {
                    // Read file from disk
                    byte[] fileData = attachmentService.readAttachmentFile(attachment.getFilePath());

                    // Encode to Base64
                    String base64Data = Base64.getEncoder().encodeToString(fileData);

                    // Create DTO with full data
                    AttachmentDTO dto = AttachmentDTO.builder()
                            .id(attachment.getId())
                            .fileName(attachment.getFileName())
                            .contentType(attachment.getContentType())
                            .fileSize(attachment.getFileSize())
                            .fileData(base64Data)
                            .build();

                    attachmentDTOs.add(dto);
                } catch (IOException e) {
                    // Log error but continue with other attachments
                    System.err.println("Failed to load attachment " + attachment.getId() + ": " + e.getMessage());
                }
            }
        }

        return EmailViewDto.builder()
                .id(mail.getMailId())
                .sender(mail.getSender())
                .receiver(mail.getReceiver())
                .subject(mail.getSubject())
                .body(mail.getBody())
                .timestamp(mail.getTimestamp())
                .priority(mail.getPriority())
                .folderName(mail.getFolderName())
                .isRead(mail.isRead())
                .attachments(attachmentDTOs)           // Full attachment data with Base64
                .build();
    }

    public List<Mail> getSortedMails(String email, String critera, boolean order){

        List<Mail> mailList = mailRepository.findByReceiverAndFolderNameOrderBySenderAsc(email, "INBOX");
        
        switch(critera){
            case "sender":
                if(order){
                   PriorityQueue<Mail> mailQueue = new PriorityQueue<Mail>(mailList.size(), 
                            ((a, b) -> a.getSender().toLowerCase().compareTo(b.getSender().toLowerCase()))
                   );
                   mailQueue.addAll(mailList);
                   mailList.clear();
                   while (!mailQueue.isEmpty())
                        mailList.add(mailQueue.poll());
                    return mailList;
                   //return mailRepository.findByReceiverAndFolderNameOrderBySenderAsc(email, "INBOX"); 
                }
                else{
                    PriorityQueue<Mail> mailQueue = new PriorityQueue<Mail>(mailList.size(), 
                                ((a, b) -> b.getSender().toLowerCase().compareTo(a.getSender().toLowerCase()))
                    );
                    mailQueue.addAll(mailList);
                    mailList.clear();
                    while (!mailQueue.isEmpty())
                            mailList.add(mailQueue.poll());
                    return mailList;
                    //return mailRepository.findByReceiverAndFolderNameOrderBySenderDesc(email, "INBOX");
                }
                     
                
            case "subject":
                if(order){
                    PriorityQueue<Mail> mailQueue = new PriorityQueue<Mail>(mailList.size(), 
                            ((a, b) -> a.getSubject().toLowerCase().compareTo(b.getSubject().toLowerCase()))
                   );
                   mailQueue.addAll(mailList);
                   mailList.clear();
                   while (!mailQueue.isEmpty())
                        mailList.add(mailQueue.poll());
                    return mailList;
                    //return mailRepository.findByReceiverAndFolderNameOrderBySubjectAsc(email, "INBOX");
                }
 
                else{
                    PriorityQueue<Mail> mailQueue = new PriorityQueue<Mail>(mailList.size(), 
                                ((a, b) -> b.getSubject().toLowerCase().compareTo(a.getSubject().toLowerCase()))
                    );
                    mailQueue.addAll(mailList);
                    mailList.clear();
                    while (!mailQueue.isEmpty())
                            mailList.add(mailQueue.poll());
                    return mailList;
                    //return mailRepository.findByReceiverAndFolderNameOrderBySubjectDesc(email, "INBOX"); 
                }
                    

            case "date":
                if(order){
                    PriorityQueue<Mail> mailQueue = new PriorityQueue<Mail>(mailList.size(), 
                            ((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()))
                    );
                    mailQueue.addAll(mailList);
                    mailList.clear();
                    while (!mailQueue.isEmpty())
                            mailList.add(mailQueue.poll());
                        return mailList;
                    //return mailRepository.findByReceiverAndFolderNameOrderByTimestampAsc(email, "INBOX");
                }
                else{
                    PriorityQueue<Mail> mailQueue = new PriorityQueue<Mail>(mailList.size(), 
                                ((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                    );
                    mailQueue.addAll(mailList);
                    mailList.clear();
                    while (!mailQueue.isEmpty())
                            mailList.add(mailQueue.poll());
                    return mailList;
                    //return mailRepository.findByReceiverAndFolderNameOrderByTimestampDesc(email, "INBOX"); 
                }

            case "priority":
                if(order){
                    // Priority Mode ON: Show HIGH priority (5) first, then lower priorities
                    PriorityQueue<Mail> mailQueue = new PriorityQueue<Mail>(mailList.size(), 
                                ((a, b) -> Integer.valueOf(b.getPriority()).compareTo(Integer.valueOf(a.getPriority())))
                    );
                    mailQueue.addAll(mailList);
                    mailList.clear();
                    while (!mailQueue.isEmpty())
                            mailList.add(mailQueue.poll());
                    return mailList;
                    //return mailRepository.findByReceiverAndFolderNameOrderByPriorityDesc(email, "INBOX");
                }
                   
                else{
                    // Priority Mode OFF: Show LOW priority (1) first
                    PriorityQueue<Mail> mailQueue = new PriorityQueue<Mail>(mailList.size(), 
                            ((a, b) -> Integer.valueOf(a.getPriority()).compareTo(Integer.valueOf(b.getPriority())))
                    );
                    mailQueue.addAll(mailList);
                    mailList.clear();
                    while (!mailQueue.isEmpty())
                            mailList.add(mailQueue.poll());
                    return mailList;
                    //return mailRepository.findByReceiverAndFolderNameOrderByPriorityAsc(email, "INBOX");
                }
                    

            default:
                return mailRepository.findByReceiver(email);
        }
    }

    // ==================== CUSTOM FOLDERS MANAGEMENT ====================

    public List<String> getUserFolders(String userEmail) {
        eg.edu.alexu.cse.mail_server.Entity.User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));
        return user.getFolders() != null ? new ArrayList<>(user.getFolders()) : new ArrayList<>();
    }

    public void createUserFolder(String userEmail, String folderName) {
        eg.edu.alexu.cse.mail_server.Entity.User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));
        
        if (user.getFolders() == null) {
            user.setFolders(new ArrayList<>());
        }
        
        if (user.getFolders().contains(folderName)) {
            throw new IllegalArgumentException("Folder already exists: " + folderName);
        }
        
        user.getFolders().add(folderName);
        userRepository.save(user);
    }

    public void deleteUserFolder(String userEmail, String folderName) {
        eg.edu.alexu.cse.mail_server.Entity.User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));
        
        if (user.getFolders() != null) {
            user.getFolders().remove(folderName);
            userRepository.save(user);
        }
        
        // Delete all emails in this folder
        Long userId = user.getUserId();
        List<Mail> folderMails = mailRepository.findByOwnerIdAndFolderNameOrderByTimestampDesc(userId, folderName);
        if (!folderMails.isEmpty()) {
            mailRepository.deleteAll(folderMails);
        }
    }

    public void renameUserFolder(String userEmail, String oldName, String newName) {
        eg.edu.alexu.cse.mail_server.Entity.User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));
        
        if (user.getFolders() == null || !user.getFolders().contains(oldName)) {
            throw new IllegalArgumentException("Folder not found: " + oldName);
        }
        
        if (user.getFolders().contains(newName)) {
            throw new IllegalArgumentException("Folder already exists: " + newName);
        }
        
        // Update folder list
        user.getFolders().remove(oldName);
        user.getFolders().add(newName);
        userRepository.save(user);
        
        // Update all emails in this folder
        Long userId = user.getUserId();
        List<Mail> folderMails = mailRepository.findByOwnerIdAndFolderNameOrderByTimestampDesc(userId, oldName);
        for (Mail mail : folderMails) {
            mail.setFolderName(newName);
        }
        if (!folderMails.isEmpty()) {
            mailRepository.saveAll(folderMails);
        }
    }

    // ==================== UPDATE DRAFT ====================

    /**
     * Update an existing draft email
     * @param draftId ID of the draft to update
     * @param dto Updated email data
     */
    public void updateDraft(Long draftId, ComposeEmailDTO dto) {
        Mail existingDraft = getMailById(draftId);
        
        // Verify it's actually a draft
        if (!"DRAFTS".equalsIgnoreCase(existingDraft.getFolderName())) {
            throw new IllegalArgumentException("Email is not a draft");
        }
        
        // Update draft fields
        existingDraft.setReceiver(String.join(", ", dto.getReceivers()));
        existingDraft.setSubject(dto.getSubject());
        existingDraft.setBody(dto.getBody());
        existingDraft.setPriority(dto.getPriority());
        existingDraft.setTimestamp(java.time.LocalDateTime.now());
        
        mailRepository.save(existingDraft);
    }

}


