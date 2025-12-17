package eg.edu.alexu.cse.mail_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // Enable scheduled tasks for automatic trash cleanup
public class MailServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MailServerApplication.class, args);
	}

}
