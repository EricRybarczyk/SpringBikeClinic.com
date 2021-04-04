package com.springbikeclinic.web.services.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class MessageContentServiceImpl implements MessageContentService {

    @Value("${verification.email.subject}")
    private String verificationEmailSubject;

    @Value("${password.reset.email.subject}")
    private String passwordResetEmailSubject;

    @Override
    public MessageContent getUserVerificationMessageBody(String verificationUrl) {
        MessageContent messageContent = new MessageContent();

        messageContent.setSubject(verificationEmailSubject);

        String intro = "Greetings from Spring Bike Clinic";
        String instructions = "Visit this URL to verify your account: ";
        String closing = "If you did not request this account, no action is required and you may safely delete this message.";

        messageContent.setPlainTextBody(generateTextMessageBody(intro, instructions, verificationUrl, closing));
        messageContent.setHtmlBody(generateHtmlMessageBody(intro, instructions, verificationUrl, closing));

        return messageContent;
    }

    @Override
    public MessageContent getPasswordResetMessageBody(String resetUrl) {
        MessageContent messageContent = new MessageContent();

        messageContent.setSubject(passwordResetEmailSubject);

        String intro = "Greetings from Spring Bike Clinic";
        String instructions = "Visit this URL to reset your password: ";
        String closing = "If you did not request a password reset, no action is required and you may safely delete this message.";

        messageContent.setPlainTextBody(generateTextMessageBody(intro, instructions, resetUrl, closing));
        messageContent.setHtmlBody(generateHtmlMessageBody(intro, instructions, resetUrl, closing));

        return messageContent;
    }

    private String generateTextMessageBody(String intro, String instructions, String url, String closing) {
        StringBuilder text = new StringBuilder();
        text.append(intro);
        text.append("\r\n \r\n");
        text.append(instructions);
        text.append(url);
        text.append("\r\n \r\n");
        text.append(closing);
        return text.toString();
    }

    private String generateHtmlMessageBody(String intro, String instructions, String url, String closing) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head></head><body><h3>");
        html.append(intro);
        html.append("</h3><p>");
        html.append(instructions);
        html.append("<a href=\"");
        html.append(url);
        html.append("\">");
        html.append(url);
        html.append("</a></p><p>");
        html.append(closing);
        html.append("</p></body></html>");
        return html.toString();
    }

}
