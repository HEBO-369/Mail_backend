package eg.edu.alexu.cse.mail_server.Service;

import eg.edu.alexu.cse.mail_server.Entity.Contact;
import eg.edu.alexu.cse.mail_server.Entity.User;
import eg.edu.alexu.cse.mail_server.Repository.ContactRepository;
import eg.edu.alexu.cse.mail_server.Repository.UserRepository;
import eg.edu.alexu.cse.mail_server.dto.ContactDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public void addContact(ContactDTO dto, String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        if (dto.getEmails() == null || dto.getEmails().isEmpty()) {
            throw new IllegalArgumentException("Contact must have at least one email address");
        }

        for (String contactEmail : dto.getEmails()) {
            if (!isValidEmail(contactEmail)) {
                throw new IllegalArgumentException("Invalid email address: " + contactEmail);
            }

            // Verify that the contact email exists in the database
            if (!userRepository.findByEmail(contactEmail).isPresent()) {
                throw new IllegalArgumentException("Email not found in database: " + contactEmail);
            }
        }

        Contact contact = Contact.builder()
                            .name(dto.getName())
                            .emails(dto.getEmails())
                            .user(user)
                            .build();

        contactRepository.save(contact);
    }

    
    public void editContact(ContactDTO newDto){

        if(newDto.getId() == null)
            throw new IllegalArgumentException("Contact ID cannot be null");

        Contact contact = contactRepository.findById(newDto.getId())
                .orElseThrow(() -> new RuntimeException("Contact not found with id: " + newDto.getId()));

        if (newDto.getEmails() == null || newDto.getEmails().isEmpty()) {
            throw new IllegalArgumentException("Contact must have at least one email address");
        }

        for (String contactEmail : newDto.getEmails()) {
            if (!isValidEmail(contactEmail)) {
                throw new IllegalArgumentException("Invalid email address: " + contactEmail);
            }

            // Verify that the contact email exists in the database
            if (!userRepository.findByEmail(contactEmail).isPresent()) {
                throw new IllegalArgumentException("Email not found in database: " + contactEmail);
            }
        }

        contact.setName(newDto.getName());
        contact.setEmails(newDto.getEmails());

        contactRepository.save(contact);
    }
    

    public void deleteContact(Long id){
        System.out.println("***************************************");
        System.out.println("DELETED");
        System.out.println("***************************************");
        contactRepository.deleteById(id);
    }

    public List<Contact> getContacts(String email, String sort){
        if(sort.equals("false"))
            return contactRepository.findByUser_EmailOrderByNameDesc(email);
        else if(sort.equals("true"))
            return contactRepository.findByUser_EmailOrderByNameAsc(email);
        else
            return contactRepository.findByUser_Email(email);
    }

    /**
     * Search contacts by name or email address
     * @param userEmail The email of the user whose contacts to search
     * @param searchQuery The search term (name or email)
     * @return List of contacts matching the search query
     */
    public List<Contact> searchContacts(String userEmail, String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return contactRepository.findByUser_Email(userEmail);
        }

        // Search by name
        List<Contact> nameMatches = contactRepository.findByUser_EmailAndNameContainingIgnoreCase(userEmail, searchQuery);

        // Search by email address
        List<Contact> emailMatches = contactRepository.findByUser_EmailAndEmailsContainingIgnoreCase(userEmail, searchQuery);

        // Combine results and remove duplicates
        nameMatches.addAll(emailMatches);
        return nameMatches.stream()
                .distinct()
                .toList();
    }

    /**
     * Validate email address format
     * @param email Email address to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}