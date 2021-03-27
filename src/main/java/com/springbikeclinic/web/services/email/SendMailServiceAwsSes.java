package com.springbikeclinic.web.services.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${aws.region.default}")
    private String awsRegion;

    @Override
    public void sendMessage(SendMailParameters sendMailParameters) {
        try {
            SesClient client = SesClient.builder()
                    .region(Region.of(awsRegion))
                    .build();

            send(client, sendMailParameters);
            client.close();

        } catch (IOException | MessagingException e) {
            log.error("Error sending email.", e);
        }
    }

    private void send(SesClient client, SendMailParameters sendMailParameters) throws MessagingException, IOException {
        // source: https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/example_code/ses

        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(session);

        message.setSubject(sendMailParameters.getMessageContent().getSubject(), "UTF-8");
        message.setFrom(new InternetAddress(sendMailParameters.getSenderAddress()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sendMailParameters.getRecipientAddress()));

        // Create a multipart/alternative child container
        MimeMultipart messageBody = new MimeMultipart("alternative");

        // Create a wrapper for the HTML and text parts
        MimeBodyPart wrap = new MimeBodyPart();

        // Define the text part
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(sendMailParameters.getMessageContent().getPlainTextBody(), "text/plain; charset=UTF-8");

        // Define the HTML part
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(sendMailParameters.getMessageContent().getHtmlBody(), "text/html; charset=UTF-8");

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
