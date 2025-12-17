package eg.edu.alexu.cse.mail_server.Service.Factory;

import eg.edu.alexu.cse.mail_server.Repository.UserRepository;
import eg.edu.alexu.cse.mail_server.Service.Strategy.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilterFactory {
    private final UserRepository userRepository;

    @Autowired
    public FilterFactory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public FilterStrategy createFilter(String filter) {
        return switch (filter) {
            case "sender"-> new SenderFilter(userRepository) ;
            case "receiver" -> new ReceiverFilter(userRepository) ;
            case "subject" -> new SubjectFilter() ;
            case "body" -> new BodyFilter() ;
            case "priority" -> new PriorityFilter() ;
            case "exactDate" -> new ExactDateFilter() ;
            case "beforeDate" -> new BeforeDateFilter() ;
            case "afterDate" -> new AfterDataFilter() ;
            case "isRead" -> new IsReadFilter() ;
            case "folder" -> new FolderFilter() ;
            case "hasAttachments" -> new HasAttachement() ;
            default -> throw new IllegalArgumentException("undefined filter");
        } ;
    }
}
