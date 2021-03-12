package com.springbikeclinic.web.dto;

import com.springbikeclinic.web.domain.BikeType;
import lombok.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BikeDto {

    @NotNull
    private Long id;

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 50, message = "Must be between {min} and {max} characters long")
    private String description;

    private BikeType bikeType;
    private CustomerDto customer;

    @NotNull
    private Integer modelYear;

    @NotNull
    @Size(min = 3, max = 50, message = "Must be between {min} and {max} characters long")
    private String manufacturerName;

    @NotNull
    @Size(min = 3, max = 50, message = "Must be between {min} and {max} characters long")
    private String modelName;

}
