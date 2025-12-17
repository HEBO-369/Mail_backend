package eg.edu.alexu.cse.mail_server.Service.Strategy;

import eg.edu.alexu.cse.mail_server.Entity.Mail;
import eg.edu.alexu.cse.mail_server.Entity.User;
import eg.edu.alexu.cse.mail_server.Repository.UserRepository;

import java.util.*;

public class ReceiverFilter implements FilterStrategy {

    private String[] receivers;
    private UserRepository repo ;

    public ReceiverFilter(String[] receivers) {
        this.receivers = receivers;
    }

    public ReceiverFilter(UserRepository repo) {
        this.repo = repo;
    }

    public String[] getReceivers() {
        return receivers;
    }

    public void setReceivers(String[] receivers) {
        this.receivers = receivers;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers.toArray(new String[0]);
    }

    @Override
    public boolean filter(Mail mail) {

        // Using optional to avoid polluting code with null checks
        List<User> mailReceivers = getReceivers(mail) ;

        if (receivers == null || receivers.length == 0) return false ;

        for (String receiver : receivers)
        {
            String query = receiver.trim().toLowerCase();
            for (User user : mailReceivers) {
                if (matchesReceiver(user,query)) return true;
            }
        }
        return false;  // No match found
    }


    private boolean matchesReceiver(User receiver, String query) {
        String fullName = String.join(" ",
                receiver.getFirstName(),
                receiver.getLastName()
        ).toLowerCase();

        String email = receiver.getEmail().toLowerCase();
        String emailLocalPart = email.split("@")[0];

        // Check if query matches as a substring in full name or email
        if (fullName.contains(query) || email.contains(query) || emailLocalPart.contains(query)) {
            return true;
        }

        // Token-based prefix matching
        String[] receiverTokens = (fullName + " " + emailLocalPart).split("[\\s@._+-]+");
        String[] queryTokens = query.split("\\s+");

        boolean allTokensMatched = true;
        for (String q : queryTokens) {
            if (q.isEmpty()) continue;
            boolean matched = Arrays.stream(receiverTokens)
                    .anyMatch(s -> !s.isEmpty() && s.startsWith(q));
            if (!matched) {
                allTokensMatched = false;
                break;
            }
        }

        return allTokensMatched;
    }

    @Override
    public int getScore(Mail mail) {
        List<User> mailReceivers = getReceivers(mail) ;

        int maxScore = 0;
        // Currently the max score
        // can be updated for a better score matching
        for (String receiver : receivers) {
            String query = receiver.trim().toLowerCase();
            for (User user : mailReceivers) {
                int score = calculateMatchScore(user, query);
                maxScore = Math.max(maxScore, score);
            }
        }
        return maxScore;
    }

    private int calculateMatchScore(User receiver, String query) {
        String fullName = String.join(" ",
                receiver.getFirstName(),
                receiver.getLastName()
        ).toLowerCase();

        String email = receiver.getEmail().toLowerCase();
        String emailLocalPart = email.split("@")[0];

        // Exact email match - highest score
        if (email.equals(query)) {
            return 100;
        }

        // Exact name match
        if (fullName.equals(query)) {
            return 90;
        }

        // Exact local part match
        if (emailLocalPart.equals(query)) {
            return 80;
        }

        // Email starts with query
        if (email.startsWith(query)) {
            return 70;
        }

        // Name starts with query
        if (fullName.startsWith(query)) {
            return 60;
        }

        // Email contains query
        if (email.contains(query)) {
            return 50;
        }

        // Name contains query
        if (fullName.contains(query)) {
            return 40;
        }

        // Token-based prefix matching
        String[] receiverTokens = (fullName + " " + emailLocalPart).split("[\\s@._+-]+");
        String[] queryTokens = query.split("\\s+");

        int matchedTokens = 0;
        for (String q : queryTokens) {
            boolean matched = Arrays.stream(receiverTokens)
                    .anyMatch(s -> s.startsWith(q));
            if (matched) matchedTokens++;
        }

        if (matchedTokens == queryTokens.length) {
            return 30; // All tokens matched
        }

        return 0;
    }

    // Currently the mail support one receiver
    List<User> getReceivers(Mail mail) {
        String[] mails = mail.getReceiver().split(",") ;
        List<User> receivers = new ArrayList<>();
        for (String receiverStr : mails) {
            Optional<User> receiver = repo.findByEmail(receiverStr.trim()) ;
            if(receiver.isEmpty()) throw new NoSuchElementException("Receiver not found");
            receivers.add(receiver.get());
        }
        return receivers ;
    }

}

