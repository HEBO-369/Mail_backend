package eg.edu.alexu.cse.mail_server.Service.Strategy;

import eg.edu.alexu.cse.mail_server.Entity.Mail;

import java.util.Objects;

public class FolderFilter implements FilterStrategy{
    private String folder ;
    @Override
    public boolean filter(Mail mail) {
        return Objects.equals(mail.getFolderName(), folder);
    }

    @Override
    public int getScore(Mail mail) {
        return filter(mail) ? 100 : 0;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
