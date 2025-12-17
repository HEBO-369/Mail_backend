package eg.edu.alexu.cse.mail_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for sending attachment data to the frontend
 * Includes file content encoded as Base64 string
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentDTO {

    private Long id;

    private String fileName;

    private String contentType;

    private Long fileSize;

    /**
     * File content encoded as Base64 string
     * Frontend can decode this to display/download the file
     */
    private String fileData;
}

