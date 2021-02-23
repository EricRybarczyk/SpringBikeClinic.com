package com.springbikeclinic.web;

import com.springbikeclinic.web.domain.WorkType;
import com.springbikeclinic.web.domain.security.Authority;
import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.repositories.WorkTypeRepository;
import com.springbikeclinic.web.repositories.security.AuthorityRepository;
import com.springbikeclinic.web.repositories.security.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final WorkTypeRepository workTypeRepository;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    public DataInitializer(WorkTypeRepository workTypeRepository, UserRepository userRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        this.workTypeRepository = workTypeRepository;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("******* Starting data initializer process...");

        if (workTypeRepository.count() == 0) {
            loadWorkTypes();
        }

        if (authorityRepository.count() == 0) {
            loadSecurityData();
        }

        log.info("******* Completed data initializer process.");
    }

    private void loadSecurityData() {
        Authority authorityMechanic = authorityRepository.save(Authority.builder().role("MECHANIC").build());
        Authority authorityCustomer = authorityRepository.save(Authority.builder().role("CUSTOMER").build());

        userRepository.save(User.builder()
                .email("mech@spring.dev")
                .password(passwordEncoder.encode("wrench"))
                .firstName("Johnny")
                .lastName("Craftsman")
                .authority(authorityMechanic)
                .build());

        userRepository.save(User.builder()
                .email("bike@bike.com")
                .password(passwordEncoder.encode("password"))
                .firstName("Rad")
                .lastName("Rider")
                .authority(authorityCustomer)
                .build());

        log.debug("Users Loaded: " + userRepository.count());

    }

    private void loadWorkTypes() {
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

        log.debug("Work Types Loaded: " + workTypeRepository.count());
    }

}
