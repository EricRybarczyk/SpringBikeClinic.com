package com.springbikeclinic.web.services.email;

public interface MessageContentService {

    MessageContent getUserVerificationMessageBody(String verificationUrl);
}
