package com.springbikeclinic.web.commands;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AccountCommand {

    private String createUsername;
    private String createPassword;
    private String confirmPassword;

}
