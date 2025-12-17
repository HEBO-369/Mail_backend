package eg.edu.alexu.cse.mail_server.Service.Strategy;

import eg.edu.alexu.cse.mail_server.Entity.Mail;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Optional;

// Currently this filter perform or search
// if any word matches it will return true
public class SubjectFilter implements FilterStrategy{
    private String query ;

    public SubjectFilter(String query) {
        this.query = query.toLowerCase().trim();
    }

    public SubjectFilter() {
    }

    @Override
    public boolean filter(Mail mail) {
        String subject = Optional.ofNullable(mail.getSubject()).
                orElse("").
                toLowerCase();

        // Splitting the query into words
        // any punctuation is valid
        String[] words = query.toLowerCase().split("[\\s,.;:!?]+") ;

        for (String word : words) if (subject.contains(word)) return true;
        return false;
    }

    /**
     * Returns a score based on how closely the mail's subject matches the query.
     * Exact match: 100
     * Partial word matches: 10 points each (max 90)
     * Substring matches: 5 points each (max 50)
     */

    public int getScore(Mail mail) {
        String subject = Optional.ofNullable(mail.getSubject()).
                orElse("").
                toLowerCase();
        if (subject.equals(query)) return 100 ;

        // Splitting the query into words
        // any punctuation is valid
        String[] words = query.toLowerCase().split("[\\s,.;:!?]+") ;

        // Calculating the number of exact partial matches
        int partialMatches = 0 ;
        for (String word : words) if (subject.contains(word)) partialMatches++ ;

        if (partialMatches > 0) return Math.min(partialMatches*10 , 90) ;

        // Calculating the number of partial words matches
        int wordPartialMatches = 0 ;
        for (String word : words) {
            for (int i = 1 ; i < word.length() ; i++ ){
                String sub = word.substring(0, i);
                if (subject.contains(sub)) wordPartialMatches++ ;
            }
        };
        if (wordPartialMatches > 0) return Math.min(wordPartialMatches*5 , 50);

        return 0;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
