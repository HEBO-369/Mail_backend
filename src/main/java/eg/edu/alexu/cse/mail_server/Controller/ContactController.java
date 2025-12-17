package eg.edu.alexu.cse.mail_server.Controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eg.edu.alexu.cse.mail_server.dto.ContactDTO;

import eg.edu.alexu.cse.mail_server.Entity.Contact;

import eg.edu.alexu.cse.mail_server.Service.ContactService;

import java.util.List;
import java.util.Map;


import org.springframework.web.bind.annotation.DeleteMapping;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mail")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @GetMapping("/contacts")
    public List<Contact> getContacts(@RequestParam Map<String, Object> body){

        return contactService.getContacts((String) body.get("email"), (String) body.get("sort"));
    }

    @PostMapping("/contacts")
    public void addContacts(@RequestBody ContactDTO contact, @RequestParam String userEmail){
        contactService.addContact(contact, userEmail);
    }

    @PutMapping("/contacts")
    public void editContacts(@RequestBody ContactDTO contact){
        contactService.editContact(contact);
    }

    @DeleteMapping("/contacts/{contactId}")
    public void deleteContacts(@PathVariable Long contactId){
        contactService.deleteContact(contactId);
    }

    @GetMapping("/contact-search")
    public List<Contact> searchContact(
            @RequestParam String userEmail,
            @RequestParam String query
    ) {
        return contactService.searchContacts(userEmail, query);
    }
}
