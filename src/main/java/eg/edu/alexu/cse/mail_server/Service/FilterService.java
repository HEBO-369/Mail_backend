package eg.edu.alexu.cse.mail_server.Service;

import eg.edu.alexu.cse.mail_server.Entity.Mail;
import eg.edu.alexu.cse.mail_server.Repository.MailRepository;
import eg.edu.alexu.cse.mail_server.Repository.UserRepository;
import eg.edu.alexu.cse.mail_server.Service.Decorator.AndDecorator;
import eg.edu.alexu.cse.mail_server.Service.Decorator.OrDecorator;
import eg.edu.alexu.cse.mail_server.Service.Factory.FilterBuilder;
import eg.edu.alexu.cse.mail_server.Service.Strategy.*;
import eg.edu.alexu.cse.mail_server.dto.EmailViewDto;
import eg.edu.alexu.cse.mail_server.dto.MailFilterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class represent filter service
 * it supports search with multiple attributes
 * via and , or (simple and / or for now)
 * it takes MailFilter DTO and based on non-null
 * fields it chooses a group of filters to apply
 * it returns EmailView DTO
 *
 */
@Service
public class FilterService {
    private final MailRepository mailRepository;
    private final MailFilter mailFilter;
    private final FilterBuilder filterBuilder ;

    @Autowired
    public FilterService(MailRepository mailRepository, FilterBuilder filterBuilder) {
        this.mailRepository = mailRepository;
        this.filterBuilder = filterBuilder;
        this.mailFilter = new MailFilter() ;

    }

    /**
     * Filter emails using AND logic - all criteria must match
     * Only returns emails related to the specified user (as sender or receiver)
     */
    public List<EmailViewDto> getEmailsAnd(MailFilterDTO dto) {
        // Validate userId is provided
        if (dto.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required for filtering");
        }

        // Get only emails related to this user
        List<Mail> mails = mailRepository.findAllByUserId(dto.getUserId());
        List<FilterStrategy> activeFilters = buildFilters(dto) ;

        if (activeFilters.isEmpty()) throw new IllegalArgumentException("Invalid filters");

        FilterStrategy filter = combineFilters(activeFilters,true) ;
        mailFilter.setFilterStrategy(filter);
        return convertToDTO(mailFilter.getEmails(mails)) ;
    }



    /**
     * Filter emails using OR logic - at least one criterion must match
     * Only returns emails related to the specified user (as sender or receiver)
     */
    public List<EmailViewDto> getEmailsOr(MailFilterDTO dto) {
        // Validate userId is provided
        if (dto.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required for filtering");
        }

        // Get only emails related to this user
        List<Mail> mails = mailRepository.findAllByUserId(dto.getUserId());
        List<FilterStrategy> activeFilters = buildFilters(dto) ;

        if (activeFilters.isEmpty()) throw new IllegalArgumentException("Invalid filters");

        FilterStrategy filter = combineFilters(activeFilters,false) ;
        mailFilter.setFilterStrategy(filter);
        return convertToDTO(mailFilter.getEmails(mails)) ;
    }

    private EmailViewDto toDTO(Mail mail) {
        EmailViewDto dto = new EmailViewDto();
        dto.setId(mail.getMailId());
        dto.setSender(mail.getSenderRel().getFirstName() + " " + mail.getSenderRel().getLastName());
        dto.setSubject(mail.getSubject());
        dto.setBody(mail.getBody());
        dto.setTimestamp(mail.getTimestamp());
        dto.setRead(mail.isRead());
        return dto;
    }

    private List<EmailViewDto> convertToDTO(List<Mail> mails) {
        List<EmailViewDto> dtos = new ArrayList<>();
        for (Mail mail : mails) {
            dtos.add(toDTO(mail));
        }
        return dtos;
    }

    private List<FilterStrategy> buildFilters(MailFilterDTO filterDTO) {
        return filterBuilder
                .withSenderFilter(filterDTO.getSender())
                .withSubjectFilter(filterDTO.getSubject())
                .withBodyFilter(filterDTO.getBody())
                .withAfterDateFilter(filterDTO.getAfterDate())
                .withBeforeDateFilter(filterDTO.getBeforeDate())
                .withExactDateFilter(filterDTO.getExactDate())
                .withPriorityFilter(filterDTO.getPriority())
                .withIsReadFilter(filterDTO.getIsRead())
                .withReceiverFilter(filterDTO.getReceiver())
                .withFolderFilter(filterDTO.getFolder())
                .withHasAttachmentsFilter(filterDTO.getHasAttachments())
                .build();
    }

    private FilterStrategy combineFilters(List<FilterStrategy> filters, boolean useAnd) {
        FilterStrategy combined = filters.get(0);
        for (int i = 1; i < filters.size(); i++) {
            combined = useAnd
                    ? new AndDecorator(combined, filters.get(i))
                    : new OrDecorator(combined, filters.get(i));
        }
        return combined;
    }

}
