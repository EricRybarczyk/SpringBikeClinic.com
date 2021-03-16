package com.springbikeclinic.web.repositories;

import com.springbikeclinic.web.domain.Bike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BikeRepository extends JpaRepository<Bike, Long> {

    List<Bike> findAllByUserId(Long userId);
    Optional<Bike> findBikeByIdAndUserId(Long bikeId, Long userId);
}
