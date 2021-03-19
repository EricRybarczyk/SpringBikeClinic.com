package com.springbikeclinic.web.mappers;

import com.springbikeclinic.web.domain.WorkItem;
import com.springbikeclinic.web.dto.WorkItemDto;
import org.mapstruct.Mapper;

// TODO: probably remove this mapper, not going to use it because not a 1-to-1 mapping thing, the Service will do more work


@Mapper
public interface WorkItemMapper {

    WorkItemDto workItemToWorkItemDto(WorkItem workItem);

    WorkItem workItemDtoToWorkItem(WorkItemDto workItemDto);

}
