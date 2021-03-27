package com.springbikeclinic.web.services.email;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageContent {

    private String subject;
    private String plainTextBody;
    private String htmlBody;

}
