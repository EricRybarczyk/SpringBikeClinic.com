package com.springbikeclinic.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SendMailParameters {

    private String senderAddress;
    private String recipientAddress;
    private String subject;
    private String message;

}
