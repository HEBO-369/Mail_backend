package eg.edu.alexu.cse.mail_server.Service.Factory;

import eg.edu.alexu.cse.mail_server.Service.Strategy.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class FilterBuilder {

    private final FilterFactory filterFactory;

    private SenderFilter senderFilter;
    private ReceiverFilter receiverFilter;
    private BodyFilter bodyFilter;
    private SubjectFilter subjectFilter;
    private PriorityFilter priorityFilter;
    private ExactDateFilter exactDateFilter;
    private BeforeDateFilter beforeDateFilter;
    private AfterDataFilter afterDateFilter;
    private IsReadFilter isReadFilter;
    private FolderFilter folderFilter;
    private HasAttachement hasAttachments;

    @Autowired
    public FilterBuilder(FilterFactory filterFactory) {
        this.filterFactory = filterFactory;
    }

    // Builder methods
    public FilterBuilder withSenderFilter(List<String> senders) {
        if (senders == null || senders.isEmpty()) return this;
        senderFilter = (SenderFilter) filterFactory.createFilter("sender");
        senderFilter.setSenderNames(senders);
        return this;
    }

    public FilterBuilder withReceiverFilter(List<String> receivers) {
        if (receivers == null || receivers.isEmpty()) return this;
        receiverFilter = (ReceiverFilter) filterFactory.createFilter("receiver");
        receiverFilter.setReceivers(receivers);
        return this;
    }

    public FilterBuilder withBodyFilter(String body) {
        if (body == null || body.isEmpty()) return this;
        bodyFilter = (BodyFilter) filterFactory.createFilter("body");
        bodyFilter.setBody(body);
        return this;
    }

    public FilterBuilder withSubjectFilter(String subject) {
        if (subject == null || subject.isEmpty()) return this;
        subjectFilter = (SubjectFilter) filterFactory.createFilter("subject");
        subjectFilter.setQuery(subject);
        return this;
    }

    public FilterBuilder withPriorityFilter(Integer priority) {
        if (priority == null) return this;
        priorityFilter = (PriorityFilter) filterFactory.createFilter("priority");
        priorityFilter.setPriority(priority);
        return this;
    }

    public FilterBuilder withExactDateFilter(LocalDateTime exactDate) {
        if (exactDate == null) return this;
        exactDateFilter = (ExactDateFilter) filterFactory.createFilter("exactDate");
        exactDateFilter.setDate(exactDate);
        return this;
    }

    public FilterBuilder withBeforeDateFilter(LocalDateTime beforeDate) {
        if (beforeDate == null) return this;
        beforeDateFilter = (BeforeDateFilter) filterFactory.createFilter("beforeDate");
        beforeDateFilter.setDate(beforeDate);
        return this;
    }

    public FilterBuilder withAfterDateFilter(LocalDateTime afterDate) {
        if (afterDate == null) return this;
        afterDateFilter = (AfterDataFilter) filterFactory.createFilter("afterDate");
        afterDateFilter.setDate(afterDate);
        return this;
    }

    public FilterBuilder withIsReadFilter(Boolean isRead) {
        if (isRead == null) return this;
        isReadFilter = (IsReadFilter) filterFactory.createFilter("isRead");
        isReadFilter.setRead(isRead);
        return this;
    }

    public FilterBuilder withFolderFilter(String folder) {
        if (folder == null || folder.isEmpty()) return this;
        folderFilter = (FolderFilter) filterFactory.createFilter("folder");
        folderFilter.setFolder(folder);
        return this;
    }

    public FilterBuilder withHasAttachmentsFilter(Boolean hasAttachmentsFlag) {
        if (hasAttachmentsFlag == null) return this;
        hasAttachments = (HasAttachement) filterFactory.createFilter("hasAttachments");
        hasAttachments.setHasAttachments(hasAttachmentsFlag);
        return this;
    }

    // Build method: return all active filters as a list
    public List<FilterStrategy> build() {
        List<FilterStrategy> filters = new java.util.ArrayList<>();
        if (senderFilter != null) filters.add(senderFilter);
        if (receiverFilter != null) filters.add(receiverFilter);
        if (bodyFilter != null) filters.add(bodyFilter);
        if (subjectFilter != null) filters.add(subjectFilter);
        if (priorityFilter != null) filters.add(priorityFilter);
        if (exactDateFilter != null) filters.add(exactDateFilter);
        if (beforeDateFilter != null) filters.add(beforeDateFilter);
        if (afterDateFilter != null) filters.add(afterDateFilter);
        if (isReadFilter != null) filters.add(isReadFilter);
        if (folderFilter != null) filters.add(folderFilter);
        if (hasAttachments != null) filters.add(hasAttachments);
        return filters;
    }
}
