package eg.edu.alexu.cse.mail_server.Service.Strategy;

import eg.edu.alexu.cse.mail_server.Entity.Mail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Optional;

public class IsReadFilter implements FilterStrategy {

    @Getter
    @Setter
    boolean isRead ;
    public IsReadFilter() {
    }

    @Override
    public boolean filter(Mail mail) {
        boolean state = Optional.of(mail.isRead()).orElse(false);
        return state == isRead;
    }

    public int getScore(Mail mail) {
        boolean state = Optional.of(mail.isRead()).orElse(false);
        return state == isRead ? 100 : 0;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
