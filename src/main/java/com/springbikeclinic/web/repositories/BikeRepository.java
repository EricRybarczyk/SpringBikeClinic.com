package com.springbikeclinic.web.repositories;

import com.springbikeclinic.web.domain.Bike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface BikeRepository extends JpaRepository<Bike, Long> {

    @Query("from Bike where user.id = :userId and deleteDateTime is null")
    List<Bike> findAllByUserId(Long userId);

    @Query("from Bike where id = :bikeId and user.id = :userId and deleteDateTime is null")
    Optional<Bike> findBikeByIdAndUserId(Long bikeId, Long userId);
}
