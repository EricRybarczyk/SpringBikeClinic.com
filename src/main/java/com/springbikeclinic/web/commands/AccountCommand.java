package com.springbikeclinic.web.commands;

import lombok.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCommand {

    @NotNull
    @NotEmpty
    @Email(regexp = ".+@.+\\..+")
    private String email;

    @NotNull
    @NotEmpty
    @Min(value = 8, message = "Must be at least 8 characters")
    private String password;

    @NotNull
    @NotEmpty
    @Min(value = 8, message = "Must be at least 8 characters")
    private String confirmPassword;

    @NotNull
    @NotEmpty
    private String firstName;

    @NotNull
    @NotEmpty
    private String lastName;

}
