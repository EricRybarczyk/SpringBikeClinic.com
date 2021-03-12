package com.springbikeclinic.web.mappers;

import com.springbikeclinic.web.domain.WorkOrder;
import com.springbikeclinic.web.dto.WorkOrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper
public interface WorkOrderMapper {

    WorkOrder workOrderDtoToWorkOrder(WorkOrderDto workOrderDto);

    // WorkOrderDto only needs Bike associated with the WorkOrder, not all of a Customer's Bikes
    @Mapping(target = "customer.bikes", ignore = true)
    WorkOrderDto workOrderToWorkOrderDto(WorkOrder workOrder);

}
