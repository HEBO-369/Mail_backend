package eg.edu.alexu.cse.mail_server.Controller;

import eg.edu.alexu.cse.mail_server.Entity.Attachment;
import eg.edu.alexu.cse.mail_server.Repository.AttachmentRepository;
import eg.edu.alexu.cse.mail_server.Service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controller for handling attachment downloads
 */
@RestController
@RequestMapping("/api/mail/attachments")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AttachmentController {
    
    private final FileStorageService fileStorageService;
    private final AttachmentRepository attachmentRepository;

    /**
     * Download an attachment by its stored filename
     * 
     * @param storedFileName The unique filename stored on disk
     * @return ResponseEntity with file content and appropriate headers
     * @throws IOException if file cannot be loaded
     */
    @GetMapping("/{storedFileName}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable String storedFileName) throws IOException {
        // Find attachment in database to get metadata
        Attachment attachment = attachmentRepository.findByStoredFileName(storedFileName)
                .orElseThrow(() -> new RuntimeException("Attachment not found: " + storedFileName));
        
        // Load file as resource
        Resource resource = fileStorageService.loadFileAsResource(storedFileName, attachment.getMail().getMailId());
        
        // Determine content type
        String contentType = attachment.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            contentType = "application/octet-stream";
        }
        
        // Determine Content-Disposition header
        // For images and PDFs, use "inline" so they open in browser
        // For other files, use "attachment" to trigger download
        String contentDisposition;
        if (isInlineableContentType(contentType)) {
            // Display inline in browser
            contentDisposition = "inline; filename=\"" + encodeFilename(attachment.getFileName()) + "\"";
        } else {
            // Trigger download
            contentDisposition = "attachment; filename=\"" + encodeFilename(attachment.getFileName()) + "\"";
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

    /**
     * Get attachment by ID (alternative endpoint)
     * 
     * @param id The attachment ID
     * @return ResponseEntity with file content and appropriate headers
     * @throws IOException if file cannot be loaded
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<Resource> downloadAttachmentById(@PathVariable Long id) throws IOException {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found with id: " + id));
        
        return downloadAttachment(attachment.getStoredFileName());
    }

    /**
     * Check if content type should be displayed inline in browser
     * 
     * @param contentType The MIME type
     * @return true if should display inline, false if should download
     */
    private boolean isInlineableContentType(String contentType) {
        if (contentType == null) {
            return false;
        }
        
        // Images should display inline
        if (contentType.startsWith("image/")) {
            return true;
        }
        
        // PDFs should display inline
        if (contentType.equals("application/pdf")) {
            return true;
        }
        
        // Text files can display inline
        if (contentType.startsWith("text/")) {
            return true;
        }
        
        // Videos can play inline
        if (contentType.startsWith("video/")) {
            return true;
        }
        
        // Audio can play inline
        if (contentType.startsWith("audio/")) {
            return true;
        }
        
        // Everything else should download
        return false;
    }

    /**
     * Encode filename for Content-Disposition header to handle special characters
     * 
     * @param filename The original filename
     * @return URL-encoded filename
     */
    private String encodeFilename(String filename) {
        return URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
