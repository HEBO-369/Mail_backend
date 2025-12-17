package eg.edu.alexu.cse.mail_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactDTO {
    
    private Long id;

    private String name;

    private List<String> emails;

}
