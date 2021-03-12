package com.springbikeclinic.web.mappers;

import com.springbikeclinic.web.domain.Mechanic;
import com.springbikeclinic.web.dto.MechanicDto;
import org.mapstruct.Mapper;

@Mapper
public interface MechanicMapper {

    Mechanic mechanicDtoToMechanic(MechanicDto mechanicDto);

    MechanicDto mechanicToMechanicDto(Mechanic mechanic);

}
