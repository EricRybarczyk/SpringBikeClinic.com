package com.springbikeclinic.web.mappers;

import com.springbikeclinic.web.domain.WorkItem;
import com.springbikeclinic.web.dto.WorkItemDto;
import org.mapstruct.Mapper;


@Mapper
public interface WorkItemMapper {

    WorkItemDto workItemToWorkItemDto(WorkItem workItem);

    WorkItem workItemDtoToWorkItem(WorkItemDto workItemDto);

}
