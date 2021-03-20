package com.springbikeclinic.web.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id")
    private WorkOrder workOrder;

    private LocalDateTime createdDateTime;
    private LocalDateTime completedDateTime;

    @Enumerated(value = EnumType.STRING)
    private WorkItemStatus status;

    private String description;
    private String mechanicNotes;

    private BigDecimal price;

}
