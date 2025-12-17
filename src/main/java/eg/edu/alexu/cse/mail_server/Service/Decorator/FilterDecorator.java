package eg.edu.alexu.cse.mail_server.Service.Decorator;

import eg.edu.alexu.cse.mail_server.Entity.Mail;
import eg.edu.alexu.cse.mail_server.Service.Strategy.FilterStrategy;

public abstract class FilterDecorator implements FilterStrategy {
    private FilterStrategy wrappedFilterStrategy;

    public FilterDecorator(FilterStrategy wrappedFilterStrategy) {
        this.wrappedFilterStrategy = wrappedFilterStrategy;
    }

    public FilterStrategy getWrappedFilterStrategy() {
        return wrappedFilterStrategy;
    }

    public void setWrappedFilterStrategy(FilterStrategy wrappedFilterStrategy) {
        this.wrappedFilterStrategy = wrappedFilterStrategy;
    }

    public boolean filter(Mail mail) {
        return wrappedFilterStrategy.filter(mail);
    };

}
