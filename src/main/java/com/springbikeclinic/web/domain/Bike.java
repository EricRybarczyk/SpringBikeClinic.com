package com.springbikeclinic.web.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springbikeclinic.web.domain.security.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "bikes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bike {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String description;

    @Enumerated(value = EnumType.STRING)
    private BikeType bikeType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "bike", fetch = FetchType.LAZY)
    private Set<WorkOrder> workOrders = new HashSet<>();

    private Integer modelYear;
    private String manufacturerName;
    private String modelName;
}
