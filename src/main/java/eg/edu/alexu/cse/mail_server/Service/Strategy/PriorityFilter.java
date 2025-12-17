package eg.edu.alexu.cse.mail_server.Service.Strategy;

import eg.edu.alexu.cse.mail_server.Entity.Mail;

import java.util.Optional;


public class PriorityFilter implements FilterStrategy {
    private int priority;

    public PriorityFilter() {
    }

    public PriorityFilter(int priority) {
        this.priority = priority;
    }

    @Override
    public boolean filter(Mail mail) {
        int mailPriority = Optional.of(mail.getPriority()).
                orElseThrow(()->new IllegalStateException("mail priority is not set"));
        int diff = Math.abs(mailPriority - this.priority);

        // Pass emails with exact or close priority
        return diff <= 2;
    }

    /**
     * Returns a score based on how close the mail's priority is to the target.
     * Exact match = 100, ±1 = 70, ±2 = 40, farther = 0
     */
    public int getScore(Mail mail) {
        int mailPriority = Optional.of(mail.getPriority()).
                orElseThrow(()->new IllegalStateException("mail priority is not set"));
        int diff = Math.abs(mailPriority - priority);

        // Assuming priority is int from 1 to 5
        if (diff == 0) return 100 ;
        if (diff == 1) return 75;
        if (diff == 2) return 50;

        return 0 ;
    }
    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

}
