package com.springbikeclinic.web.mappers;

import com.springbikeclinic.web.domain.Bike;
import com.springbikeclinic.web.dto.BikeDto;
import org.mapstruct.Mapper;

@Mapper
public interface BikeMapper {

    BikeDto bikeToBikeDto(Bike bike);

    Bike bikeDtoToBike(BikeDto bikeDto);

}

