package com.springbikeclinic.web.services;


import com.springbikeclinic.web.domain.Bike;
import com.springbikeclinic.web.dto.BikeDto;
import java.util.Set;

public interface BikeService {

    Bike save(BikeDto bikeDto, Long userId);
    Set<Bike> getBikes(Long userId);
    Bike getBikeForUser(Long bikeId, Long userId);
}
