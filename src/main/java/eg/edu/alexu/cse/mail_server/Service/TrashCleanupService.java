package eg.edu.alexu.cse.mail_server.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Scheduled task to automatically delete emails that have been in trash for more than 30 days
 */
@Service
@RequiredArgsConstructor
public class TrashCleanupService {

    private final MailService mailService;

    /**
     * Runs every day at 2:00 AM to delete old trash emails
     * Cron expression: second, minute, hour, day of month, month, day of week
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupOldTrashEmails() {
        System.out.println("Running scheduled trash cleanup task...");
        mailService.deleteOldTrashEmails();
    }

    /**
     * Alternative: Run every 24 hours (uncomment if you prefer this over cron)
     * @Scheduled(fixedRate = 86400000) // 24 hours in milliseconds
     */
}

