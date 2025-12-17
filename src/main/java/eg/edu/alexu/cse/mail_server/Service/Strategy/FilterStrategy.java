package eg.edu.alexu.cse.mail_server.Service.Strategy;

import eg.edu.alexu.cse.mail_server.Entity.Mail;

/**
 * The current Design for the filters return boolean for each email
 * This design allows for easy combination of different filters
 * Using Decorators
 * */
public interface FilterStrategy {

    public boolean filter(Mail mail);

    public int getScore(Mail mail);
}
