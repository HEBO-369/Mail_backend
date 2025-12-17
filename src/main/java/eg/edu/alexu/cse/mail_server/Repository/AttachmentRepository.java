package eg.edu.alexu.cse.mail_server.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eg.edu.alexu.cse.mail_server.Entity.Attachment;


@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    /**
     * Find all attachments by mail ID
     * Uses the 'mail' field in Attachment entity and accesses its 'mailId' property
     */
    List<Attachment> findByMailMailId(Long mailId);

    Optional<Attachment> findByStoredFileName(String storedFileName);
}
