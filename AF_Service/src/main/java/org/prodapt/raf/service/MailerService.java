package org.prodapt.raf.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.prodapt.raf.dto.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
public class MailerService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public void sendEmail(Mail mail) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(mail.getProps());
    
        String html = templateEngine.process(mail.getProps().get("entity").toString(), context);
        if(!Arrays.toString(mail.getMailTo()).contentEquals("[]")) {
        	helper.setTo(mail.getMailTo());
        }
        if(!Arrays.toString(mail.getMailCc()).contentEquals("[]")) {
        	helper.setCc(mail.getMailCc());
        }
        if(!Arrays.toString(mail.getMailBcc()).contentEquals("[]")) {
        	helper.setBcc(mail.getMailBcc());
        }
        helper.setText(html, true);
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());

        emailSender.send(message);
    }

}
