package com.springbikeclinic.web.dto;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Getter
@Setter
public class ResetPasswordResultDto {

    private String token;

    @NotNull
    @NotEmpty
    @Size(min = 8, max = 255, message = "Must be at least 8 characters")
    private String newPassword;

    @NotNull
    @NotEmpty
    @Size(min = 8, max = 255, message = "Must be at least 8 characters")
    private String confirmPassword;

    public ResetPasswordResultDto() {
    }

    public ResetPasswordResultDto(String token) {
        this.token = token;
    }

}
