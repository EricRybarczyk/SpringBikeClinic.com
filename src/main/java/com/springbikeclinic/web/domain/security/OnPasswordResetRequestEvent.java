package com.springbikeclinic.web.domain.security;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnPasswordResetRequestEvent extends ApplicationEvent {

    private final User user;
    private final String passwordResetPath;

    public OnPasswordResetRequestEvent(Object source, User user, String passwordResetPath) {
        super(source);
        this.user = user;
        this.passwordResetPath = passwordResetPath;
    }

}
