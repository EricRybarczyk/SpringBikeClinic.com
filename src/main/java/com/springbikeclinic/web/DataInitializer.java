package com.springbikeclinic.web;

import com.springbikeclinic.web.domain.security.Authority;
import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.dto.WorkTypeDto;
import com.springbikeclinic.web.mappers.WorkTypeMapper;
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
    private final WorkTypeMapper workTypeMapper;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    public DataInitializer(WorkTypeRepository workTypeRepository, WorkTypeMapper workTypeMapper, UserRepository userRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        this.workTypeRepository = workTypeRepository;
        this.workTypeMapper = workTypeMapper;
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

        WorkTypeDto workTypeTuneUp = WorkTypeDto.builder()
                .name("Tune Up")
                .description("Our best seller, the Tune Up will keep your wheels turning all season long. Includes derailleur adjustments, drive train cleaning, brake service, and a 17-point inspection. Replacement parts not included.")
                .price(BigDecimal.valueOf(99.95))
                .sortPriority(1)
                .build();
        workTypeRepository.save(workTypeMapper.workTypeDtoToWorkType(workTypeTuneUp));

        WorkTypeDto workTypeDerailleur = WorkTypeDto.builder()
                .name("Derailleur Adjustment")
                .description("Front or rear derailleur tuning. We'll get your shift together! Replacement parts not included.")
                .price(BigDecimal.valueOf(19.95))
                .sortPriority(2)
                .build();
        workTypeRepository.save(workTypeMapper.workTypeDtoToWorkType(workTypeDerailleur));

        WorkTypeDto workTypeFlatTire = WorkTypeDto.builder()
                .name("Flat Tire")
                .description("Get back on your bike after we replace the tube in your flat tire. Includes replacement tube.")
                .price(BigDecimal.valueOf(44.95))
                .sortPriority(3)
                .build();
        workTypeRepository.save(workTypeMapper.workTypeDtoToWorkType(workTypeFlatTire));

        WorkTypeDto workTypeTrueWheel = WorkTypeDto.builder()
                .name("True a Wheel")
                .description("Feeling a bit wobbly? Truing a wheel restores the straight path in front of your bike. Replacement parts not included.")
                .price(BigDecimal.valueOf(39.95))
                .sortPriority(4)
                .build();
        workTypeRepository.save(workTypeMapper.workTypeDtoToWorkType(workTypeTrueWheel));

        WorkTypeDto workTypeSuspension = WorkTypeDto.builder()
                .name("Suspension Service")
                .description("Keep the plush feeling as you hit the big drops by maintaining this critical component. Front fork or rear shock service available. Replacement parts not included.")
                .price(BigDecimal.valueOf(89.95))
                .sortPriority(5)
                .build();
        workTypeRepository.save(workTypeMapper.workTypeDtoToWorkType(workTypeSuspension));

        WorkTypeDto workTypeBrakeBleed = WorkTypeDto.builder()
                .name("Brake Bleed")
                .description("Get those hydraulics back in tip top shape with fresh fluid and a firm lever feel. Includes brake safety inspection.")
                .price(BigDecimal.valueOf(49.95))
                .sortPriority(6)
                .build();
        workTypeRepository.save(workTypeMapper.workTypeDtoToWorkType(workTypeBrakeBleed));

        WorkTypeDto workTypeBrakeService = WorkTypeDto.builder()
                .name("Brake Service")
                .description("Caliper alignment, sticky pistons, worn pads or cables... We'll make sure you can stop when you need to. Replacement parts not included.")
                .price(BigDecimal.valueOf(39.95))
                .sortPriority(7)
                .build();
        workTypeRepository.save(workTypeMapper.workTypeDtoToWorkType(workTypeBrakeService));


        log.debug("Work Types Loaded: " + workTypeRepository.count());
    }

}
