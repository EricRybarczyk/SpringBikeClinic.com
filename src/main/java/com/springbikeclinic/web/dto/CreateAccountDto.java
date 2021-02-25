package com.springbikeclinic.web.dto;

import com.springbikeclinic.web.security.validation.PasswordMatches;
import lombok.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@PasswordMatches(message = "Both passwords must match")
public class CreateAccountDto {

    @NotNull
    @NotEmpty
    @Email(regexp = ".+@.+\\..+")
    private String email;

    @NotNull
    @NotEmpty
    @Size(min = 8, max = 255, message = "Must be at least 8 characters")
    private String createPassword;

    @NotNull
    @NotEmpty
    @Size(min = 8, max = 255, message = "Must be at least 8 characters")
    private String confirmPassword;

    @NotNull
    @NotEmpty
    private String firstName;

    @NotNull
    @NotEmpty
    private String lastName;

}
