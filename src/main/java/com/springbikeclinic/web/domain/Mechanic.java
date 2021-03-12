package com.springbikeclinic.web.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "mechanics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mechanic extends Person {

    private LocalDate hireDate;

    @OneToMany(mappedBy = "mechanic", fetch = FetchType.LAZY)
    private Set<WorkOrder> workOrders = new HashSet<>();
}
