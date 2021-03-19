package com.springbikeclinic.web.domain;

import com.springbikeclinic.web.domain.security.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "work_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime createdDateTime;
    private LocalDateTime submittedDateTime;

    @Enumerated(value = EnumType.STRING)
    private WorkOrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_id")
    private Bike bike;

    private LocalDate customerDropOffDate;
    private LocalDate customerPickUpDate;
    private LocalDate estimatedCompletionDate;
    private LocalDate actualCompletionDate;
    private String customerNotes;
    private String mechanicNotes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workOrder")
    private Set<WorkItem> workItems = new HashSet<>();
}
