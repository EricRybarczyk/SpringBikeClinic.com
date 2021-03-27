package com.springbikeclinic.web.services;

import com.springbikeclinic.web.dto.SendMailParameters;

public interface SendMailService {

    void sendMessage(SendMailParameters sendMailParameters);

}
