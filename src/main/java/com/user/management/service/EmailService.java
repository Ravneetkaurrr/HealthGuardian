package com.user.management.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  @Autowired
  private JavaMailSender mailSender;

  public void sendEmail(String recipientEmail, String emailSubject, String emailBody)
      throws MessagingException {
    MimeMessage mimemessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimemessage, true);
    helper.setTo(recipientEmail);
    helper.setSubject(emailSubject);
    helper.setText(emailBody, true);
    mailSender.send(mimemessage);

  }
}
