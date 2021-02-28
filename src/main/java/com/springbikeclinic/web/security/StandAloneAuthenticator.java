package com.springbikeclinic.web.security;

import javax.servlet.http.HttpServletRequest;

public interface StandAloneAuthenticator {
    void authenticateUser(HttpServletRequest request, String username, String password);
}
