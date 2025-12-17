package eg.edu.alexu.cse.mail_server.Service.Strategy;

import java.util.Optional;

import eg.edu.alexu.cse.mail_server.Entity.Mail;

/**
 * BodyFilter implements realistic email body search behavior
 * Matches whole words and word prefixes (like Gmail/Outlook)
 */
public class BodyFilter implements FilterStrategy {

    private String body;

    public BodyFilter() {
    }

    public BodyFilter(String body) {
        this.body = body.toLowerCase().trim();
    }

    @Override
    public boolean filter(Mail mail) {
        String mailBody = Optional.ofNullable(mail.getBody()).orElse("").toLowerCase();

        if (mailBody.isEmpty() || body == null || body.isEmpty())
            return false;

        // Exact full body match
        if (mailBody.equals(body))
            return true;

        String[] queryWords = body.split("[\\s,.;:!?]+");
        String[] bodyWords = mailBody.split("[\\s,.;:!?]+");

        // Match if any query word is found as whole word or prefix in body
        for (String queryWord : queryWords) {
            if (queryWord.isEmpty())
                continue;

            for (String bodyWord : bodyWords) {
                if (bodyWord.isEmpty())
                    continue;

                // Exact match or prefix match (realistic email search)
                // Instead of old way of contains, we do startsWith for prefix matching
                if (bodyWord.equals(queryWord) || bodyWord.startsWith(queryWord)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a score based on how closely the mail's body matches the query.
     * Full exact match: 100
     * Exact word matches: 15 points each (max 90)
     * Prefix matches: 8 points each (max 50)
     */
    @Override
    public int getScore(Mail mail) {
        String mailBody = Optional.ofNullable(mail.getBody()).orElse("").toLowerCase();

        // Exact full match
        if (mailBody.equals(body)) {
            return 100;
        }

        // then check for full body contains the query exactly
        if (mailBody.contains(body)) {
            return 90;
        }

        String[] queryWords = body.split("[\\s,.;:!?]+");
        String[] bodyWords = mailBody.split("[\\s,.;:!?]+");

        int exactMatches = 0;
        int prefixMatches = 0;

        for (String queryWord : queryWords) {
            if (queryWord.isEmpty())
                continue;

            boolean foundExact = false;
            boolean foundPrefix = false;

            for (String bodyWord : bodyWords) {
                if (bodyWord.isEmpty())
                    continue;

                if (bodyWord.equals(queryWord)) {
                    foundExact = true;
                    break;
                } else if (!foundPrefix && bodyWord.startsWith(queryWord)) {
                    foundPrefix = true;
                }
            }

            if (foundExact) {
                exactMatches++;
            } else if (foundPrefix) {
                prefixMatches++;
            }
        }

        // Prioritize exact matches over prefix matches
        if (exactMatches > 0) {
            return Math.min(exactMatches * 15, 90);
        }
        if (prefixMatches > 0) {
            return Math.min(prefixMatches * 8, 50);
        }

        return 0;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body != null ? body.toLowerCase().trim() : null;
    }
}
