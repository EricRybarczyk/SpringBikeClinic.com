package com.springbikeclinic.web.services;

import com.springbikeclinic.web.TestData;
import com.springbikeclinic.web.domain.Bike;
import com.springbikeclinic.web.domain.WorkItem;
import com.springbikeclinic.web.domain.WorkOrder;
import com.springbikeclinic.web.domain.WorkOrderStatus;
import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.dto.BikeDto;
import com.springbikeclinic.web.dto.ServiceHistoryItem;
import com.springbikeclinic.web.dto.WorkItemDto;
import com.springbikeclinic.web.dto.WorkOrderDto;
import com.springbikeclinic.web.exceptions.NotFoundException;
import com.springbikeclinic.web.mappers.BikeMapper;
import com.springbikeclinic.web.mappers.WorkItemMapper;
import com.springbikeclinic.web.mappers.WorkOrderMapper;
import com.springbikeclinic.web.repositories.WorkItemRepository;
import com.springbikeclinic.web.repositories.WorkOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class WorkOrderServiceTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private WorkItemRepository workItemRepository;

    @Mock
    private WorkTypeService workTypeService;

    @Mock
    private BikeService bikeService;

    @Mock
    private BikeMapper bikeMapper;

    @Mock
    private WorkOrderMapper workOrderMapper;

    @Mock
    private WorkItemMapper workItemMapper;


    @InjectMocks
    private WorkOrderServiceImpl workOrderService;

    @Test
    void testCreateWorkOrder_withValidInput_workOrderCreated() throws Exception {
        when(bikeService.getBikeForUser(anyLong(), anyLong())).thenReturn(TestData.getExistingBikeDto());
        when(bikeMapper.bikeDtoToBike(any(BikeDto.class))).thenReturn(TestData.getBike());
        when(workTypeService.getWorkType(anyLong())).thenReturn(TestData.getSingleWorkType());
        when(workOrderRepository.save(any(WorkOrder.class))).thenReturn(TestData.getExistingWorkOrder());

        WorkOrderDto workOrderDto = TestData.getNewWorkOrderDto();
        User user = new User();
        user.setId(1L);

        Long resultWorkOrderId = workOrderService.createWorkOrder(workOrderDto, user);

        assertThat(resultWorkOrderId).isNotNull();

        verify(bikeService, times(1)).getBikeForUser(anyLong(), anyLong());
        verify(workTypeService, times(1)).getWorkType(anyLong());

        verify(workOrderRepository, times(1)).save(any(WorkOrder.class));
        verify(workItemRepository, times(1)).save(any(WorkItem.class));
    }

    @Test
    void testCreateWorkOrder_invalidBikeIdUserIdInput_ThrowsNotFoundException() throws Exception {
        when(bikeService.getBikeForUser(anyLong(), anyLong())).thenThrow(new NotFoundException("bike not found"));
        WorkOrderDto workOrderDto = TestData.getNewWorkOrderDto();
        User user = new User();
        user.setId(1L);

        assertThrows(NotFoundException.class, () -> workOrderService.createWorkOrder(workOrderDto, user));

        verifyNoInteractions(workOrderRepository);
        verifyNoInteractions(workItemRepository);
    }

    @Test
    void testGetServiceHistory_validUserWithData_listReturnedHasItems() throws Exception {
        final List<WorkOrder> sourceWorkOrderList = TestData.getExistingWorkOrderList();
        when(workOrderRepository.findAllByUser(any(User.class))).thenReturn(sourceWorkOrderList);

        User user = new User();
        user.setId(1L);
        final List<ServiceHistoryItem> serviceHistory = workOrderService.getServiceHistory(user);

        assertThat(serviceHistory).isNotNull();
        assertThat(serviceHistory.size()).isEqualTo(sourceWorkOrderList.size());
    }

    @Test
    void testGetServiceHistory_noDataFromRepository_listReturnedIsEmpty() throws Exception {
        when(workOrderRepository.findAllByUser(any(User.class))).thenReturn(new ArrayList<>());

        User user = new User();
        user.setId(999L);
        final List<ServiceHistoryItem> serviceHistory = workOrderService.getServiceHistory(user);

        assertThat(serviceHistory).isNotNull();
        assertThat(serviceHistory.size()).isEqualTo(0);
    }

    @Test
    void testGetWorkOrder_validRequest_workOrderReturned() throws Exception {
        when(workOrderRepository.findByIdAndUser(anyLong(), any(User.class))).thenReturn(getMockWorkOrder());
        when(workOrderMapper.workOrderToWorkOrderDto(any(WorkOrder.class))).thenReturn(getMockWorkOrderDto());
        when(bikeMapper.bikeToBikeDto(any(Bike.class))).thenReturn(TestData.getExistingBikeDto());
        when(workItemMapper.workItemToWorkItemDto(any(WorkItem.class))).thenReturn(getMockWorkOrderDto().getWorkItemDto());

        final WorkOrderDto workOrderDto = workOrderService.getWorkOrder(1L, User.builder().id(5150L).build());

        assertThat(workOrderDto).isNotNull();
        assertThat(workOrderDto.getBikeDto()).isNotNull();
        assertThat(workOrderDto.getWorkItemDto()).isNotNull();

        verify(workOrderRepository, times(1)).findByIdAndUser(anyLong(), any(User.class));
    }

    private WorkOrderDto getMockWorkOrderDto() {
        WorkOrderDto workOrder = new WorkOrderDto();
        workOrder.setId(1L);
        workOrder.setCreatedDateTime(LocalDateTime.now());
        workOrder.setCustomerDropOffDate(LocalDate.now());
        workOrder.setCustomerNotes("notes");
        workOrder.setStatus(WorkOrderStatus.SUBMITTED);
        workOrder.setSubmittedDateTime(LocalDateTime.now());

        WorkItemDto workItem = new WorkItemDto();
        workItem.setId(88L);
        workItem.setDescription("work");

        workOrder.setWorkTypeId(workItem.getId());
        workOrder.setWorkItemDto(workItem);

        return workOrder;
    }

    private Optional<WorkOrder> getMockWorkOrder() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setCreatedDateTime(LocalDateTime.now());
        workOrder.setCustomerDropOffDate(LocalDate.now());
        workOrder.setCustomerNotes("notes");
        workOrder.setStatus(WorkOrderStatus.SUBMITTED);
        workOrder.setSubmittedDateTime(LocalDateTime.now());

        workOrder.setUser(User.builder().id(999L).email("a@b.co").build());
        workOrder.setBike(TestData.getBike());

        WorkItem workItem = new WorkItem();
        workItem.setId(77L);
        workItem.setWorkOrder(workOrder);
        workOrder.getWorkItems().add(workItem);

        return Optional.of(workOrder);
    }

}
