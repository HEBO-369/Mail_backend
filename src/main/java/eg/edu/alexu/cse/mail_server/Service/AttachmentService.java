package eg.edu.alexu.cse.mail_server.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import eg.edu.alexu.cse.mail_server.Entity.Attachment;
import eg.edu.alexu.cse.mail_server.Entity.Mail;
import eg.edu.alexu.cse.mail_server.Repository.AttachmentRepository;
import eg.edu.alexu.cse.mail_server.dto.AttachmentDTO;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final FileStorageService fileStorageService;

    /**
     * Save attachment file to disk and persist metadata to database
     * 
     * @param file The multipart file to save
     * @param mail The mail entity this attachment belongs to
     * @return Saved attachment entity
     * @throws IOException if file operations fail
     */
    public Attachment saveAttachment(MultipartFile file, Mail mail) throws IOException {
        // Use FileStorageService to handle file system operations
        Attachment attachment = fileStorageService.saveFile(file, mail);
        
        // Extract text content for searching (optional)
        String indexedContent = extractTextContent(file);
        attachment.setIndexedContent(indexedContent);
        
        // Save attachment metadata to database
        return attachmentRepository.save(attachment);
    }

    /**
     * Get all attachments for a specific mail
     *
     * @param mailId the ID of the mail
     * @return list of all attachments belonging to this mail
     */
    public List<Attachment> getAttachmentsByMailId(Long mailId) {
        return attachmentRepository.findByMailMailId(mailId);
    }

    /**
     * Get all attachments with file data for a specific mail
     * This method reads the actual file content and encodes it as Base64
     * for sending to the frontend
     *
     * @param mailId the ID of the mail
     * @return list of AttachmentDTOs with file data included
     * @throws IOException if file reading fails
     */
    public List<AttachmentDTO> getAttachmentsWithData(Long mailId) throws IOException {
        List<Attachment> attachments = getAttachmentsByMailId(mailId);
        List<AttachmentDTO> attachmentDTOs = new ArrayList<>();

        for (Attachment attachment : attachments) {

            byte[] fileData = readAttachmentFile(attachment.getFilePath());

            // Encode to Base64 for JSON transfer
            String base64Data = java.util.Base64.getEncoder().encodeToString(fileData);

            AttachmentDTO dto = AttachmentDTO.builder()
                    .id(attachment.getId())
                    .fileName(attachment.getFileName())
                    .contentType(attachment.getContentType())
                    .fileSize(attachment.getFileSize())
                    .fileData(base64Data)
                    .build();

            attachmentDTOs.add(dto);
        }

        return attachmentDTOs;
    }

    /**
     * Get attachment data by attachment ID
     * (Optional - for downloading specific attachment)
     */
    public byte[] getAttachmentData(Long attachmentId) throws IOException {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        Path path = Paths.get(attachment.getFilePath());
        return Files.readAllBytes(path);
    }

    /**
     * Read file data from disk by file path
     *
     * @param filePath the path to the file on disk
     * @return file content as byte array
     */
    public byte[] readAttachmentFile(String filePath) throws IOException {
        return fileStorageService.readFileBytes(filePath);
    }

    private String extractTextContent(MultipartFile file) {

        return "";
    }

}
