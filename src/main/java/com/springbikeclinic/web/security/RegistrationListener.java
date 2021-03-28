package com.springbikeclinic.web.security;

import com.springbikeclinic.web.domain.security.OnRegistrationCompleteEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final UserVerificationService userVerificationService;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        userVerificationService.initiateUserVerification(event.getUser(), event.getVerificationPath());
    }

}
