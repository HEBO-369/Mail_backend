package eg.edu.alexu.cse.mail_server.Service.Strategy;

import eg.edu.alexu.cse.mail_server.Entity.Mail;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class takes one of email filters
 * and returns the concrete filters
 * it will also be beneficial with
 * decorator fir multiple filters search
 * Also this class can be extended easily to
 * manage search we will just use decorated filter that
 * wraps all filters (Or decorator)
 */

public class MailFilter {
    @Getter
    @Setter
    private FilterStrategy filterStrategy ;

    public MailFilter(FilterStrategy filter) {
        filterStrategy = filter;
    }

    public MailFilter() {}

    public List<Mail> getEmails(List<Mail> mails) {
        return mails.stream().
                filter(mail -> filterStrategy.filter(mail)).
                sorted((mail1,mail2) -> Integer.compare(
                        filterStrategy.getScore(mail2),
                        filterStrategy.getScore(mail1))).
                collect(Collectors.toList());
    }

    public FilterStrategy getFilterStrategy() {
        return filterStrategy;
    }

    public void setFilterStrategy(FilterStrategy filterStrategy) {
        this.filterStrategy = filterStrategy;
    }
}
