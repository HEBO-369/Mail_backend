package eg.edu.alexu.cse.mail_server.Service.Strategy;

import eg.edu.alexu.cse.mail_server.Entity.Mail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;


public class BeforeDateFilter implements FilterStrategy {

    @Getter
    @Setter
    private LocalDateTime date;

    public BeforeDateFilter() {
    }

    @Override
    public boolean filter(Mail mail) {
        LocalDateTime localDate = Optional.ofNullable(mail.getTimestamp()).
                orElseThrow(() ->new NoSuchElementException("")) ;

        return localDate.isBefore(date);
    }

    public int getScore(Mail mail) {
        long daysDifference = java.time.Duration.between(mail.getTimestamp(),date).toDays();

        // the max Difference for now one year
        long maxDifference = 12*30 ;

        // Closer emails get higher score: linear inverse scaling
        int score = (int) Math.max(0, 100 - (daysDifference * 100) / maxDifference);

        // Cap score to 0-100
        score = Math.min(score, 100);

        return score;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
