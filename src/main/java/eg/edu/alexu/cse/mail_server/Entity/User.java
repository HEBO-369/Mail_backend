package eg.edu.alexu.cse.mail_server.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "_users",
        uniqueConstraints = @UniqueConstraint(columnNames = "email_address")
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 255) // for hashing
    private String password;
    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(unique = true, name = "email_address", nullable = false)
    private String email;

    // Easy navigation between user and emails
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL , mappedBy = "senderRel")
    private List<Mail> sentEmail = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "receiverRel" , fetch = FetchType.LAZY)
    private List<Mail> receivedEmail = new ArrayList<>();

    @OneToMany(
        mappedBy = "user",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL
    )
    private List<Contact> contacts;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Mail> getSentEmail() {
        return sentEmail;
    }

    public void setSentEmail(List<Mail> sentEmail) {
        this.sentEmail = sentEmail;
    }

    public List<Mail> getReceivedEmail() {
        return receivedEmail;
    }

    public void setReceivedEmail(List<Mail> receivedEmail) {
        this.receivedEmail = receivedEmail;
    }
}