package eg.edu.alexu.cse.mail_server.Service.Strategy;

import eg.edu.alexu.cse.mail_server.Entity.Mail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;


public class ExactDateFilter implements FilterStrategy {
    private LocalDateTime date;

    public ExactDateFilter() {
    }

    public ExactDateFilter(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean filter(Mail mail) {
        LocalDateTime localDate = Optional.ofNullable(mail.getTimestamp()).
                orElseThrow(() ->new NoSuchElementException("")) ;

        return localDate.isEqual(date);
    }

    public int getScore(Mail mail) {
        LocalDateTime localDate = Optional.ofNullable(mail.getTimestamp()).
                orElseThrow(() ->new NoSuchElementException("")) ;

        return localDate.isEqual(date) ? 100 : 0;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
