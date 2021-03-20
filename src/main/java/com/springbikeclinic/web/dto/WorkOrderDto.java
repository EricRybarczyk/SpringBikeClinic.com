package com.springbikeclinic.web.dto;

import com.springbikeclinic.web.domain.WorkOrderStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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

    @NotNull
    private Long bikeId;

    @NotNull
    @FutureOrPresent
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate customerDropOffDate;

    private LocalDate customerPickUpDate;
    private LocalDate estimatedCompletionDate;
    private LocalDate actualCompletionDate;

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 255, message = "Must be between {min} and {max} characters long")
    private String customerNotes;

    @NotNull
    private Long workTypeId;
}
