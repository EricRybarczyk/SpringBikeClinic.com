package com.springbikeclinic.web.services;

import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.dto.ServiceHistoryItem;
import com.springbikeclinic.web.dto.WorkOrderDto;
import java.util.List;

public interface WorkOrderService {
    Long createWorkOrder(WorkOrderDto workOrderDto, User user);
    List<ServiceHistoryItem> getServiceHistory(User user);
    WorkOrderDto getWorkOrder(Long workOrderId, User user);
}
