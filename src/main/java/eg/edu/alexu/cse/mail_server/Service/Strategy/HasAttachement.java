package eg.edu.alexu.cse.mail_server.Service.Strategy;

import eg.edu.alexu.cse.mail_server.Entity.Mail;

public class HasAttachement implements FilterStrategy {

    private boolean hasAttachments;
    @Override
    public boolean filter(Mail mail) {
        var attach = mail.getAttachments();
        if (attach != null &&  !attach.isEmpty() && hasAttachments) return true;
        else if((attach == null||attach.isEmpty())&& !hasAttachments) return true;
        return false ;
    }

    @Override
    public int getScore(Mail mail) {
        return (filter(mail) ? 100 : 0);
    }

    public boolean isHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }
}
