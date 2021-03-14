package com.springbikeclinic.web.repositories;

import com.springbikeclinic.web.domain.Bike;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.Set;

public interface BikeRepository extends JpaRepository<Bike, Long> {

    Set<Bike> findAllByUserId(Long userId, Sort sort);
    Optional<Bike> findBikeByIdAndUserId(Long bikeId, Long userId);
}
