package eg.edu.alexu.cse.mail_server.Service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * Scheduled task to automatically delete emails that have been in trash for more than 1 minute
 * TO CHANGE CLEANUP FREQUENCY: Change fixedRate value:
 * - 60000 = 1 minute (current - for testing)
 * - 300000 = 5 minutes
 * - 3600000 = 1 hour
 * - 86400000 = 24 hours (1 day)
 */
@Service
@RequiredArgsConstructor
public class TrashCleanupService {

    private final MailService mailService;

    /**
     * Runs every 1 minute to delete old trash emails
     * Uses fixedRate for consistent interval execution
     */
    @Scheduled(fixedRate = 60000) // Run every 1 minute (60000 milliseconds)
    public void cleanupOldTrashEmails() {
        System.out.println("Running scheduled trash cleanup task...");
        mailService.deleteOldTrashEmails();
    }

}

