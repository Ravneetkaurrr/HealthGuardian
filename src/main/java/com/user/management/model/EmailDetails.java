package com.user.management.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "email_details")
public class EmailDetails {
    @Id
    private String id;
    private String emailBody;
    private String emailSubject;
    private String emailType;

    public String getId() {
        return id;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public String getEmailType() {
        return emailType;
    }
}
