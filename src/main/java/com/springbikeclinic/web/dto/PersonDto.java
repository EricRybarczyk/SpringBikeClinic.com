package com.springbikeclinic.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
abstract class PersonDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;

}

