package eg.edu.alexu.cse.mail_server.Service.Decorator;

import eg.edu.alexu.cse.mail_server.Entity.Mail;
import eg.edu.alexu.cse.mail_server.Service.Strategy.FilterStrategy;

public class AndDecorator extends  FilterDecorator {
    private FilterStrategy secondWrappedFilterStrategy;

    public AndDecorator(FilterStrategy wrappedFilterStrategy ,  FilterStrategy secondWrappedFilterStrategy) {
        super(wrappedFilterStrategy);
        this.secondWrappedFilterStrategy = secondWrappedFilterStrategy;
    }
    public FilterStrategy getSecondWrappedFilterStrategy() {
        return secondWrappedFilterStrategy;
    }
    public void setSecondWrappedFilterStrategy(FilterStrategy secondWrappedFilterStrategy) {
        this.secondWrappedFilterStrategy = secondWrappedFilterStrategy;
    }

    @Override
    public boolean filter(Mail mail) {
        return super.filter(mail) &&  secondWrappedFilterStrategy.filter(mail);
    }

    @Override
    public int getScore(Mail mail) {
        return softAnd(getWrappedFilterStrategy().getScore(mail),getSecondWrappedFilterStrategy().getScore(mail)) ;
    }
    int softAnd(int a, int b) {
        return (int) Math.pow(a / 100.0 * b / 100.0, 0.5) * 100;
    }
}
