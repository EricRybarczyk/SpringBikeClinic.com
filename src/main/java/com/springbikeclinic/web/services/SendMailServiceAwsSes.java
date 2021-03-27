package com.springbikeclinic.web.services;

import com.springbikeclinic.web.dto.SendMailParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;
import software.amazon.awssdk.services.ses.model.SesException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;


// Adapted directly from: https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/example_code/

@Service
@Slf4j
public class SendMailServiceAwsSes implements SendMailService {

    private static final Region REGION = Region.US_EAST_2;

    @Override
    public void sendMessage(SendMailParameters sendMailParameters) {
        // The email body for non-HTML email clients
        String bodyText = "Hello,\r\n" + sendMailParameters.getMessage();

        // The HTML body of the email
        String bodyHTML = "<html><head></head><body><h1>Hello!</h1><p>"
                + sendMailParameters.getMessage() + "</p></body></html>";

        SesClient client = SesClient.builder()
                .region(REGION)
                .build();

        try {
            send(client, sendMailParameters.getSenderAddress(), sendMailParameters.getRecipientAddress(), sendMailParameters.getSubject(), bodyText, bodyHTML);
            client.close();

        } catch (IOException | MessagingException e) {
            log.error("Error sending email.", e);
        }
    }

    public static void send(SesClient client, String sender, String recipient, String subject, String bodyText, String bodyHTML) throws MessagingException, IOException {
        // source: https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/example_code/ses

        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(session);

        message.setSubject(subject, "UTF-8");
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));

        // Create a multipart/alternative child container
        MimeMultipart messageBody = new MimeMultipart("alternative");

        // Create a wrapper for the HTML and text parts
        MimeBodyPart wrap = new MimeBodyPart();

        // Define the text part
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(bodyText, "text/plain; charset=UTF-8");

        // Define the HTML part
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(bodyHTML, "text/html; charset=UTF-8");

        // Add the text and HTML parts to the child container
        messageBody.addBodyPart(textPart);
        messageBody.addBodyPart(htmlPart);

        // Add the child container to the wrapper object
        wrap.setContent(messageBody);

        // Create a multipart/mixed parent container
        MimeMultipart msg = new MimeMultipart("mixed");

        // Add the parent container to the message
        message.setContent(msg);

        // Add the multipart/alternative part to the message
        msg.addBodyPart(wrap);

        try {
            log.info("Attempting to send an email through Amazon SES using the AWS SDK for Java...");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);
            ByteBuffer buf = ByteBuffer.wrap(outputStream.toByteArray());

            byte[] arr = new byte[buf.remaining()];
            buf.get(arr);

            SdkBytes data = SdkBytes.fromByteArray(arr);
            RawMessage rawMessage = RawMessage.builder()
                    .data(data)
                    .build();

            SendRawEmailRequest rawEmailRequest = SendRawEmailRequest.builder()
                    .rawMessage(rawMessage)
                    .build();

            client.sendRawEmail(rawEmailRequest);

        } catch (SesException e) {
            log.error(e.awsErrorDetails().errorMessage());
        }

    }


}
