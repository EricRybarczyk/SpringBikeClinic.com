package com.springbikeclinic.web.services;


import com.springbikeclinic.web.domain.Bike;
import java.util.Set;

public interface BikeService {

    Bike save(Bike bike, Long userId);
    Set<Bike> getBikes(Long userId);

}
