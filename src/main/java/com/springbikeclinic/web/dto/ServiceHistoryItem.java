package com.springbikeclinic.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class ServiceHistoryItem {

    private Long id;
    private LocalDate serviceDate;
    private BikeDto bikeDto;
    private String serviceDescription;
    private String status;

}
