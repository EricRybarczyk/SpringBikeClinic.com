package com.springbikeclinic.web.services;

import com.springbikeclinic.web.domain.*;
import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.dto.ServiceHistoryItem;
import com.springbikeclinic.web.dto.WorkOrderDto;
import com.springbikeclinic.web.mappers.BikeMapper;
import com.springbikeclinic.web.repositories.WorkItemRepository;
import com.springbikeclinic.web.repositories.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final WorkItemRepository workItemRepository;
    private final WorkTypeService workTypeService;
    private final BikeService bikeService;
    private final BikeMapper bikeMapper;

    @Override
    @Transactional
    public Long createWorkOrder(WorkOrderDto workOrderDto, User user) {

        final Bike bike = bikeMapper.bikeDtoToBike(bikeService.getBikeForUser(workOrderDto.getBikeId(), user.getId()));
        final WorkType workType = workTypeService.getWorkType(workOrderDto.getWorkTypeId());

        // create a WorkOrder entity from the main data in the WorkOrderDto
        WorkOrder detachedWorkOrder = new WorkOrder();
        detachedWorkOrder.setCreatedDateTime(LocalDateTime.now());
        detachedWorkOrder.setSubmittedDateTime(LocalDateTime.now());
        detachedWorkOrder.setStatus(WorkOrderStatus.SUBMITTED);
        detachedWorkOrder.setUser(user);
        detachedWorkOrder.setBike(bike);
        detachedWorkOrder.setCustomerDropOffDate(workOrderDto.getCustomerDropOffDate());
        detachedWorkOrder.setCustomerNotes(workOrderDto.getCustomerNotes());

        final WorkOrder savedWorkOrder = workOrderRepository.save(detachedWorkOrder);

        // add a WorkItem entity based on the WorkTypeDto
        WorkItem detachedWorkItem = new WorkItem();
        detachedWorkItem.setWorkOrder(savedWorkOrder);
        detachedWorkItem.setCreatedDateTime(LocalDateTime.now());
        detachedWorkItem.setStatus(WorkItemStatus.PENDING);
        detachedWorkItem.setPrice(workType.getPrice());
        // the WorkType name field is the basic type of work (don't use description because that is more like a marketing description)
        detachedWorkItem.setDescription(workType.getName());

        workItemRepository.save(detachedWorkItem);

        return savedWorkOrder.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceHistoryItem> getServiceHistory(User user) {
        final List<WorkOrder> workOrderList = workOrderRepository.findAllByUser(user);
        final List<ServiceHistoryItem> result = new ArrayList<>();

        for (WorkOrder workOrder : workOrderList) {
            // currently a WorkOrder will have a single WorkItem so we are keeping this simple for now
            final Optional<WorkItem> optionalWorkItem = workOrder.getWorkItems().stream().findFirst();
            result.add(
                    ServiceHistoryItem.builder()
                            .id(workOrder.getId())
                            .serviceDate(workOrder.getSubmittedDateTime().toLocalDate())
                            .bikeDto(bikeMapper.bikeToBikeDto(workOrder.getBike()))
                            .serviceDescription(optionalWorkItem.isPresent() ? optionalWorkItem.get().getDescription() : "SERVICE")
                            .status(workOrder.getStatus().name())
                            .build()
            );
        }

        return result;
    }

}
