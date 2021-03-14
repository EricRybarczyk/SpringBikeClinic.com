package com.springbikeclinic.web.services;

import com.springbikeclinic.web.domain.Bike;
import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.repositories.BikeRepository;
import com.springbikeclinic.web.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class BikeServiceImpl implements BikeService {

    private final BikeRepository bikeRepository;
    private final UserRepository userRepository;

    @Override
    public Set<Bike> getBikes(Long userId) {
        final Set<Bike> bikes = bikeRepository.findAllByUserId(userId, Sort.by(Sort.Order.asc("id")));

        log.debug("-------> Found {} bikes for user ID {}", bikes.size(), userId);
        bikes.forEach(b -> log.debug("-------> Bike ID: {}", b.getId()));

        return bikes;
    }

    @Override
    public Bike save(Bike bike, Long userId) {
        final User user = userRepository.findById(userId).orElseThrow();

        user.getBikes().add(bike);
        bike.setUser(user);

        return bikeRepository.save(bike);
    }


}
