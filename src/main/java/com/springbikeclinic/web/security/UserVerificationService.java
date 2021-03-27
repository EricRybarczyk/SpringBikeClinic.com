package com.springbikeclinic.web.security;

import com.springbikeclinic.web.domain.security.User;

public interface UserVerificationService {

    void initiateUserVerification(User user, String verificationUrl);

    UserVerificationResult verifyUser(String token);

}
