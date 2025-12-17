package eg.edu.alexu.cse.mail_server.Service.Decorator;

import eg.edu.alexu.cse.mail_server.Entity.Mail;
import eg.edu.alexu.cse.mail_server.Service.Strategy.FilterStrategy;

public class OrDecorator extends FilterDecorator{

    protected FilterStrategy secondFilterStrategy;
    public OrDecorator(FilterStrategy wrappedFilterStrategy ,  FilterStrategy secondFilterStrategy) {
        super(wrappedFilterStrategy);
        this.secondFilterStrategy = secondFilterStrategy;
    }

    public FilterStrategy getSecondFilterStrategy() {
        return secondFilterStrategy;
    }

    public void setSecondFilterStrategy(FilterStrategy secondFilterStrategy) {
        this.secondFilterStrategy = secondFilterStrategy;
    }

    @Override
    public boolean filter(Mail mail) {
        return super.filter(mail) || secondFilterStrategy.filter(mail);
    }

    @Override
    public int getScore(Mail mail) {
        return softOr(getWrappedFilterStrategy().getScore(mail),getSecondFilterStrategy().getScore(mail));
    }

    int softOr(int a, int b) {
        return a + b - (a * b / 100);
    }
}
