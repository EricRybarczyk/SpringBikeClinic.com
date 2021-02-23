package com.springbikeclinic.web.commands;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCommand {

    private String email;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;

}
