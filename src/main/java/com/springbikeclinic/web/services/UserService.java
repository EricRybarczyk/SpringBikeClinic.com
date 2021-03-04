package com.springbikeclinic.web.services;

import com.springbikeclinic.web.domain.security.SecurityUser;
import com.springbikeclinic.web.dto.CustomerAccountDto;

public interface UserService {
    void updateUser(SecurityUser securityUser, CustomerAccountDto customerUpdateDto);
}
