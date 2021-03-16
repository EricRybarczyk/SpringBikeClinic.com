package com.springbikeclinic.web.services;


import com.springbikeclinic.web.dto.BikeDto;
import java.util.List;

public interface BikeService {

    BikeDto save(BikeDto bikeDto, Long userId);
    List<BikeDto> getBikes(Long userId);
    BikeDto getBikeForUser(Long bikeId, Long userId);
    void deleteBikeForUser(Long bikeId, Long userId);

}
