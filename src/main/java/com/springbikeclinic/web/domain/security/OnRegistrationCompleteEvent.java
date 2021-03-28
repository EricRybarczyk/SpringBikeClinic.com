package com.springbikeclinic.web.domain.security;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private final User user;
    private final String verificationPath;

    public OnRegistrationCompleteEvent(Object source, User user, String verificationPath) {
        super(source);
        this.user = user;
        this.verificationPath = verificationPath;
    }

}
