package com.springbikeclinic.web.security;

import com.springbikeclinic.web.domain.security.PasswordResetToken;
import com.springbikeclinic.web.domain.security.User;

public interface PasswordResetService {
    void initiatePasswordReset(User user, String passwordResetHandlerPath);
    PasswordResetToken getPasswordResetToken(String token);
    void processPasswordReset(PasswordResetToken passwordResetToken, String newPassword);
}
