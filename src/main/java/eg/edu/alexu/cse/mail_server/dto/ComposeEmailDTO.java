package eg.edu.alexu.cse.mail_server.dto;

import lombok.Data;

import java.util.List;

@Data

public class ComposeEmailDTO {
    private String sender;

    private List<String> receivers;

    private String subject;
    private String body;
    private int priority;
}
