package com.springbikeclinic.web.security;

import com.springbikeclinic.web.domain.security.OnPasswordResetRequestEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PasswordResetRequestListener implements ApplicationListener<OnPasswordResetRequestEvent> {

    private final PasswordResetService passwordResetService;

    @Override
    public void onApplicationEvent(OnPasswordResetRequestEvent event) {
        passwordResetService.initiatePasswordReset(event.getUser(), event.getPasswordResetPath());
    }

}
