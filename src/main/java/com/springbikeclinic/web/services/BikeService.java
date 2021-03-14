package com.springbikeclinic.web.services;


import com.springbikeclinic.web.dto.BikeDto;
import java.util.Set;

public interface BikeService {

    BikeDto save(BikeDto bikeDto, Long userId);
    Set<BikeDto> getBikes(Long userId);
    BikeDto getBikeForUser(Long bikeId, Long userId);
}
