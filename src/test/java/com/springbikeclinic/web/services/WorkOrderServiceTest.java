package com.springbikeclinic.web.services;

import com.springbikeclinic.web.TestData;
import com.springbikeclinic.web.domain.WorkItem;
import com.springbikeclinic.web.domain.WorkOrder;
import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.dto.BikeDto;
import com.springbikeclinic.web.dto.WorkOrderDto;
import com.springbikeclinic.web.exceptions.NotFoundException;
import com.springbikeclinic.web.mappers.BikeMapper;
import com.springbikeclinic.web.repositories.WorkItemRepository;
import com.springbikeclinic.web.repositories.WorkOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

}
