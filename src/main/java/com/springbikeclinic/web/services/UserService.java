package com.springbikeclinic.web.services;

import com.springbikeclinic.web.dto.CustomerAccountDto;

public interface UserService {
    void updateUser(Long id, CustomerAccountDto customerUpdateDto);
}
