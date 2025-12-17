package eg.edu.alexu.cse.mail_server.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import eg.edu.alexu.cse.mail_server.Entity.Attachment;
import eg.edu.alexu.cse.mail_server.Entity.Mail;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * Service for handling file storage operations on the file system.
 * Stores files in the user's home directory under "mansy-mail-uploads".
 */
@Service
@RequiredArgsConstructor
public class FileStorageService {
    
    private static final String UPLOAD_DIR_NAME = "mansy-mail-uploads";
    private final Path uploadDirectory;

    public FileStorageService() {
        // Initialize upload directory path
        String userHome = System.getProperty("user.home");
        this.uploadDirectory = Paths.get(userHome, UPLOAD_DIR_NAME);
    }

    /**
     * Create the upload directory on application startup if it doesn't exist
     */
    @PostConstruct
    public void init() {
        try {
            if (!Files.exists(uploadDirectory)) {
                Files.createDirectories(uploadDirectory);
                System.out.println("Created upload directory: " + uploadDirectory.toAbsolutePath());
            } else {
                System.out.println("Upload directory exists: " + uploadDirectory.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadDirectory.toAbsolutePath(), e);
        }
    }

    /**
     * Save a file to the file system
     * 
     * @param file The multipart file to save
     * @param mail The mail entity this attachment belongs to
     * @return Attachment entity with file metadata
     * @throws IOException if file writing fails
     */
    public Attachment saveFile(MultipartFile file, Mail mail) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot save empty file");
        }

        // Generate unique filename: UUID_originalFilename
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            originalFilename = "unnamed_file";
        }
        
        String storedFileName = UUID.randomUUID().toString() + "_" + originalFilename;
        
        // Create subdirectory for this mail (optional, for organization)
        Path mailDirectory = uploadDirectory.resolve(String.valueOf(mail.getMailId()));
        if (!Files.exists(mailDirectory)) {
            Files.createDirectories(mailDirectory);
        }
        
        // Full path where file will be stored
        Path filePath = mailDirectory.resolve(storedFileName);
        
        // Write file bytes to disk
        Files.write(filePath, file.getBytes());
        
        // Build and return Attachment entity
        return Attachment.builder()
                .fileName(originalFilename)
                .storedFileName(storedFileName)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .filePath(filePath.toString())
                .uploadDate(LocalDateTime.now())
                .mail(mail)
                .build();
    }

    /**
     * Load a file from the file system as a Resource
     * 
     * @param storedFileName The unique filename stored on disk
     * @param mailId The ID of the mail (for subdirectory lookup)
     * @return Resource representing the file
     * @throws IOException if file not found or cannot be read
     */
    public Resource loadFileAsResource(String storedFileName, Long mailId) throws IOException {
        try {
            Path mailDirectory = uploadDirectory.resolve(String.valueOf(mailId));
            Path filePath = mailDirectory.resolve(storedFileName);
            
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new IOException("File not found or not readable: " + storedFileName);
            }
        } catch (Exception e) {
            throw new IOException("Error loading file: " + storedFileName, e);
        }
    }

    /**
     * Read file bytes directly from disk
     * 
     * @param filePath Full path to the file
     * @return File content as byte array
     * @throws IOException if file reading fails
     */
    public byte[] readFileBytes(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filePath);
        }
        return Files.readAllBytes(path);
    }

    /**
     * Delete a file from the file system
     * 
     * @param filePath Full path to the file
     * @throws IOException if deletion fails
     */
    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }

    /**
     * Get the upload directory path
     * 
     * @return Path to the upload directory
     */
    public Path getUploadDirectory() {
        return uploadDirectory;
    }
}
