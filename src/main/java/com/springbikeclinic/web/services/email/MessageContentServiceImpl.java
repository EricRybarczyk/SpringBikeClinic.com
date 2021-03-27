package com.springbikeclinic.web.services.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class MessageContentServiceImpl implements MessageContentService {

    @Value("${verification.email.subject}")
    private String emailSubject;

    @Override
    public MessageContent getUserVerificationMessageBody(String verificationUrl) {
        MessageContent messageContent = new MessageContent();

        messageContent.setSubject(emailSubject);

        String intro = "Greetings from Spring Bike Clinic";
        String instructions = "Visit this URL to verify your account:";
        String closing = "If you did not request this account, no action is required and you may safely delete this message.";

        StringBuilder text = new StringBuilder();
        text.append(intro);
        text.append("\r\n \r\n");
        text.append(instructions);
        text.append(verificationUrl);
        text.append("\r\n \r\n");
        text.append(closing);

        StringBuilder html = new StringBuilder();
        html.append("<html><head></head><body><h3>");
        text.append(intro);
        html.append("</h3><p>");
        html.append(instructions);
        html.append("<a href=\"");
        html.append(verificationUrl);
        html.append("\">");
        html.append(verificationUrl);
        html.append("</a></p><p>");
        html.append(closing);
        html.append("</p></body></html>");

        messageContent.setPlainTextBody(text.toString());
        messageContent.setHtmlBody(html.toString());

        return messageContent;
    }

}
