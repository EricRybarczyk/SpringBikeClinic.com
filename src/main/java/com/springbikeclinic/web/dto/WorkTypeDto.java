package com.springbikeclinic.web.dto;


import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkTypeDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer sortPriority;
}
