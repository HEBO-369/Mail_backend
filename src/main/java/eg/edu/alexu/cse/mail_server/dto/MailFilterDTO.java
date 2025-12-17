package eg.edu.alexu.cse.mail_server.dto;

import java.time.LocalDateTime;
import java.util.List;

// DTO for mail filtering - uses nullable types for optional fields
public class MailFilterDTO {
    private Long userId; // Required: The user performing the search
    private List<String> sender;
    private List<String> receiver;
    private String subject;
    private String body;
    private LocalDateTime exactDate;
    private LocalDateTime afterDate;
    private LocalDateTime beforeDate;
    private Boolean isRead;
    private Integer priority;
    private String folder ;
    private Boolean hasAttachments;
    // Attachment will be added soon

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getSender() {
        return sender;
    }

    public void setSender(List<String> sender) {
        this.sender = sender;
    }

    public List<String> getReceiver() {
        return receiver;
    }

    public void setReceiver(List<String> receiver) {
        this.receiver = receiver;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getExactDate() {
        return exactDate;
    }

    public void setExactDate(LocalDateTime exactDate) {
        this.exactDate = exactDate;
    }

    public LocalDateTime getAfterDate() {
        return afterDate;
    }

    public void setAfterDate(LocalDateTime afterDate) {
        this.afterDate = afterDate;
    }

    public LocalDateTime getBeforeDate() {
        return beforeDate;
    }

    public void setBeforeDate(LocalDateTime beforeDate) {
        this.beforeDate = beforeDate;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public Boolean getHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(Boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }
}