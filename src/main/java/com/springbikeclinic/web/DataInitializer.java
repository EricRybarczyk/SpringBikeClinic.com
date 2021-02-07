package com.springbikeclinic.web;

import com.springbikeclinic.web.domain.WorkType;
import com.springbikeclinic.web.repositories.WorkTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final WorkTypeRepository workTypeRepository;
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    public DataInitializer(WorkTypeRepository workTypeRepository) {
        this.workTypeRepository = workTypeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("******* Starting data initializer process...");

        for (int i = 1; i < 7; i++) {
            WorkType workType = new WorkType();
            workType.setName("Work Type " + i);
            workType.setDescription("This is a Service offered by Spring Bike Clinic, and it is a really good service and you should really get this excellent service for your bike today!");
            workType.setSortPriority(i);
            if (i % 2 == 1) {
                workType.setPrice(BigDecimal.valueOf(79.95));
            } else {
                workType.setPrice(BigDecimal.valueOf(119.95));
            }
            workTypeRepository.save(workType);
        }

        log.info("******* Completed data initializer process.");
    }

}
