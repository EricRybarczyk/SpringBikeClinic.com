package com.springbikeclinic.web.services.email;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SendMailParameters {

    private String senderAddress;
    private String recipientAddress;
    private MessageContent messageContent;

}
