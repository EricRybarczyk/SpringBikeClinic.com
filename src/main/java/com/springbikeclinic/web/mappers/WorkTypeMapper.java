package com.springbikeclinic.web.mappers;

import com.springbikeclinic.web.domain.WorkType;
import com.springbikeclinic.web.dto.WorkTypeDto;
import org.mapstruct.Mapper;

@Mapper
public interface WorkTypeMapper {

    WorkTypeDto workTypeToWorkTypeDto(WorkType workType);
    WorkType workTypeDtoToWorkType(WorkTypeDto workTypeDto);
}
