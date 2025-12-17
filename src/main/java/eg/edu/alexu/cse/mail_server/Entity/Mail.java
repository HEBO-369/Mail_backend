package eg.edu.alexu.cse.mail_server.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Mail {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mailId;

    @Column(nullable = false)
    private String sender;
    @Column(nullable = false)
    private String receiver;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(nullable = false)
    private String subject;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @Builder.Default
    private int priority = 1;

    @Column(nullable = false)
    private String folderName; // inbox, sent, trash, draft

    @Builder.Default
    private boolean isRead = false; // for ui

    // Track when email was moved to trash for automatic deletion after 30 days
    private LocalDateTime deletedAt;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "mail_id")
    private List<Attachment> attachments;

    // Update preserved the scheme but added extra fields
    // Changed the users and emails relations
    // The current scheme is better in terms of
    // Relations it will also ease the process of
    // Navigating between emails and users
    // Currently the relations are bidirectional
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id" , nullable = false)
    private User senderRel ;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "mail_recivers",
            joinColumns = @JoinColumn(name = "mail_id"),
            inverseJoinColumns = @JoinColumn(name = "receiver_id")
    )
    private List<User> receiverRel = new ArrayList<>();

    // Owner of this mail copy (for trash, drafts, and folder management)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "owner_id", insertable = false, updatable = false)
    private Long ownerId;

    public Long getMailId() {
        return mailId;
    }

    public void setMailId(Long mailId) {
        this.mailId = mailId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public User getSenderRel() {
        return senderRel;
    }

    public void setSenderRel(User senderRel) {
        this.senderRel = senderRel;
    }

    public List<User> getReceiverRel() {
        return receiverRel;
    }

    public void setReceiverRel(List<User> receiverRel) {
        this.receiverRel = receiverRel;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}