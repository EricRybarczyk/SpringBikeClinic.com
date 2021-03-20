package com.springbikeclinic.web.dto;

import com.springbikeclinic.web.domain.BikeType;
import com.springbikeclinic.web.dto.validation.BikeModelYear;
import lombok.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BikeDto {

    private Long id;

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 50, message = "Must be between {min} and {max} characters long")
    private String description;

    private BikeType bikeType;

    @NotNull
    @BikeModelYear(message = "Must be a valid Bike Model Year")
    private Integer modelYear;

    @NotNull
    @Size(min = 3, max = 50, message = "Must be between {min} and {max} characters long")
    private String manufacturerName;

    @NotNull
    @Size(min = 3, max = 50, message = "Must be between {min} and {max} characters long")
    private String modelName;

    private LocalDateTime deleteDateTime;

    public boolean isActive() {
        return deleteDateTime == null;
    }

    public boolean isNew() {
        return id == null;
    }
}
