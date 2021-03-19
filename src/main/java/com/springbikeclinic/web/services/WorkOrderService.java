package com.springbikeclinic.web.services;

import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.dto.WorkOrderDto;

public interface WorkOrderService {
    Long createWorkOrder(WorkOrderDto workOrderDto, User user);
}
