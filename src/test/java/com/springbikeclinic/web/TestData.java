package com.springbikeclinic.web;

import com.springbikeclinic.web.domain.Bike;
import com.springbikeclinic.web.domain.BikeType;
import com.springbikeclinic.web.domain.WorkType;
import com.springbikeclinic.web.dto.BikeDto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TestData {

    public static List<WorkType> getWorkTypesList() {
        List<WorkType> result = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            WorkType workType = new WorkType();
            workType.setName("Work Type " + i);
            workType.setDescription("This is a Service offered by Spring Bike Clinic, and it is a really good service and you should really get this excellent service for your bike today!");
            workType.setSortPriority(i);
            workType.setPrice(BigDecimal.valueOf(19.95));
            result.add(workType);
        }
        return result;
    }

    public static WorkType getSingleWorkType() {
        WorkType workType = new WorkType();
        workType.setName("Sample Work Type");
        workType.setDescription("Description of the Sample Work Type");
        workType.setSortPriority(0);
        workType.setPrice(BigDecimal.valueOf(9.99));
        return workType;
    }



    public static Bike getBike() {
        Bike bike = new Bike();
        bike.setId(1L);
        bike.setBikeType(BikeType.MOUNTAIN);
        bike.setDescription("description");
        bike.setManufacturerName("manufacturer");
        bike.setModelName("model");
        bike.setModelYear(2020);
        return bike;
    }

    public static BikeDto getNewBikeDto() {
        return BikeDto.builder()
                .bikeType(BikeType.MOUNTAIN)
                .description("bike")
                .manufacturerName("manufacturer")
                .modelName("model")
                .modelYear(2020)
                .build();
    }

    public static BikeDto getExistingBikeDto() {
        return BikeDto.builder()
                .id(1L)
                .bikeType(BikeType.MOUNTAIN)
                .description("bike")
                .manufacturerName("manufacturer")
                .modelName("model")
                .modelYear(2020)
                .build();
    }
}
