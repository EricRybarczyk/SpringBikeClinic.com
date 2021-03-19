package com.springbikeclinic.web.dto;

import com.springbikeclinic.web.domain.WorkOrderStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class WorkOrderDto {
    private Long id;
    private LocalDateTime createdDateTime;
    private LocalDateTime submittedDateTime;
    private WorkOrderStatus status;
    private Long customerId;
    private Long bikeId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate customerDropOffDate;
    private LocalDate customerPickUpDate;
    private LocalDate estimatedCompletionDate;
    private LocalDate actualCompletionDate;
    private String customerNotes;
    private Long workTypeId;
}
