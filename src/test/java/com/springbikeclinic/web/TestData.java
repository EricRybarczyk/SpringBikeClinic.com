package com.springbikeclinic.web;

import com.springbikeclinic.web.domain.WorkType;
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
}
