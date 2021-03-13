package com.springbikeclinic.web.dto;

import com.springbikeclinic.web.domain.WorkOrderStatus;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class WorkOrderDto {
    private Long id;
    private LocalDateTime createdDateTime;
    private LocalDateTime submittedDateTime;
    private WorkOrderStatus status;

    private BikeDto bike;
    private LocalDate estimatedCompletionDate;
    private LocalDate actualCompletionDate;
    private String customerNotes;
    private Set<WorkItemDto> workItems;
}

