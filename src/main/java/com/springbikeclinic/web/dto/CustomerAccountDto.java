package com.springbikeclinic.web.dto;

import com.springbikeclinic.web.domain.security.SecurityUser;
import lombok.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.security.Principal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerAccountDto {

    @NotNull
    @NotEmpty
    @Email(regexp = ".+@.+\\..+")
    private String email;

    @NotNull
    @NotEmpty
    private String firstName;

    @NotNull
    @NotEmpty
    private String lastName;

    public static CustomerAccountDto from(Principal principal) {
        SecurityUser user = SecurityUser.from(principal);
        return CustomerAccountDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getUsername())
                .build();
    }
}
