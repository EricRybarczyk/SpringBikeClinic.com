package com.springbikeclinic.web.repositories;

import com.springbikeclinic.web.domain.Bike;
import com.springbikeclinic.web.domain.BikeType;
import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.repositories.security.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BikeRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BikeRepository bikeRepository;

    private Long USER_ONE_ID;
    private Long USER_TWO_ID;

    private Long BIKE_ID_NOT_DELETED_FOR_USER_ONE;


    @BeforeEach
    void setUp() {
        // two users, two bikes each, one bike for each user with deleted date

        // set up one user with two bikes, one bike with a delete date
        final User savedUser = userRepository.save(getDetachedUser());
        USER_ONE_ID = savedUser.getId();

        final Bike detachedBike1 = getDetachedBike();
        detachedBike1.setUser(savedUser);
        final Bike savedNotDeletedBike = bikeRepository.save(detachedBike1);
        BIKE_ID_NOT_DELETED_FOR_USER_ONE = savedNotDeletedBike.getId();

        // second bike, with a deleted date
        final Bike detachedBike2 = getDetachedBike();
        detachedBike2.setUser(savedUser);
        detachedBike2.setDeleteDateTime(LocalDateTime.now());
        bikeRepository.save(detachedBike2);

        // second user, two bikes, one with delete date
        final User savedUser2 = userRepository.save(getDetachedUser());
        USER_TWO_ID = savedUser2.getId();

        final Bike detachedBike3 = getDetachedBike();
        detachedBike3.setUser(savedUser2);
        bikeRepository.save(detachedBike3);

        // deleted bike for user 2
        final Bike detachedBike4 = getDetachedBike();
        detachedBike4.setUser(savedUser2);
        detachedBike4.setDeleteDateTime(LocalDateTime.now());
        bikeRepository.save(detachedBike4);
    }

    @Test
    void findAllByUserId_resultContainsNoDeletedBikes() throws Exception {
        final List<Bike> userOneBikes = bikeRepository.findAllByUserId(USER_ONE_ID);
        assertThat(userOneBikes.size()).isEqualTo(1);
        assertThat(userOneBikes.get(0).getDeleteDateTime()).isNull();

        final List<Bike> userTwoBikes = bikeRepository.findAllByUserId(USER_TWO_ID);
        assertThat(userTwoBikes.size()).isEqualTo(1);
        assertThat(userTwoBikes.get(0).getDeleteDateTime()).isNull();
    }

    @Test
    void findBikeByIdAndUserId_resultBikeHasNoDeleteDate() throws Exception {
        final Optional<Bike> optionalBike = bikeRepository.findBikeByIdAndUserId(BIKE_ID_NOT_DELETED_FOR_USER_ONE, USER_ONE_ID);
        assertThat(optionalBike.isPresent()).isTrue();
        final Bike bike = optionalBike.get();
        assertThat(bike.getDeleteDateTime()).isNull();
    }

    private User getDetachedUser() {
        return User.builder()
                .firstName("First")
                .lastName("Last")
                .email("a@b.com")
                .build();
    }

    private Bike getDetachedBike() {
        Bike bike = new Bike();
        bike.setBikeType(BikeType.MOUNTAIN);
        bike.setDescription("description");
        bike.setManufacturerName("manufacturer");
        bike.setModelName("model");
        bike.setModelYear(2020);
        return bike;
    }

}
