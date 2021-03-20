package com.springbikeclinic.web.controllers;

import com.springbikeclinic.web.TestData;
import com.springbikeclinic.web.domain.WorkType;
import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.dto.WorkOrderDto;
import com.springbikeclinic.web.exceptions.NotFoundException;
import com.springbikeclinic.web.security.WithMockCustomUser;
import com.springbikeclinic.web.services.BikeService;
import com.springbikeclinic.web.services.WorkOrderService;
import com.springbikeclinic.web.services.WorkTypeServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServicesController.class)
class ServicesControllerTest {

    private static final String GET_SERVICES_PATH = "/services";
    private static final String GET_SCHEDULE_SERVICE_PATH = "/services/schedule";
    private static final String POST_SCHEDULE_SERVICE_PATH = "/services/schedule/save";
    private static final String EXPECTED_GET_SERVICES_VIEW_NAME = "services";
    private static final String EXPECTED_GET_SCHEDULE_SERVICE_VIEW_NAME = "scheduleService";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkTypeServiceImpl workTypeService;

    @MockBean
    private BikeService bikeService;

    @MockBean
    private WorkOrderService workOrderService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @WithMockUser("authenticatedUser")
    @Test
    void getServices_asAuthenticatedUser_IsOk() throws Exception {
        final List<WorkType> workTypes = TestData.getWorkTypesList();
        when(workTypeService.listWorkTypes()).thenReturn(workTypes);

        mockMvc.perform(get(GET_SERVICES_PATH))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("workTypeList"))
                .andExpect(model().attribute("workTypeList", workTypes))
                .andExpect(view().name(EXPECTED_GET_SERVICES_VIEW_NAME));
    }

    @Test
    void getServices_asAnonymousUser_IsOk() throws Exception {
        final List<WorkType> workTypes = TestData.getWorkTypesList();
        when(workTypeService.listWorkTypes()).thenReturn(workTypes);

        mockMvc.perform(get(GET_SERVICES_PATH))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("workTypeList"))
                .andExpect(model().attribute("workTypeList", workTypes))
                .andExpect(view().name(EXPECTED_GET_SERVICES_VIEW_NAME));
    }

    @WithMockCustomUser
    @Test
    void beginScheduleService_asAuthenticatedUser_IsOk() throws Exception {
        final WorkType workType = TestData.getSingleWorkType();
        when(workTypeService.getWorkType(anyLong())).thenReturn(workType);

        mockMvc.perform(get(GET_SCHEDULE_SERVICE_PATH + "/1")).andExpect(status().isOk())
                .andExpect(model().attributeExists("workType"))
                .andExpect(model().attribute("workType", workType))
                .andExpect(view().name(EXPECTED_GET_SCHEDULE_SERVICE_VIEW_NAME));
    }

    @Test
    void beginScheduleService_asAnonymousUser_IsUnauthorized() throws Exception {
        final WorkType workType = TestData.getSingleWorkType();
        when(workTypeService.getWorkType(anyLong())).thenReturn(workType);

        mockMvc.perform(get(GET_SCHEDULE_SERVICE_PATH + "/1"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockCustomUser
    @Test
    void requestServicePost_withValidInput_asAuthenticatedUser_isOk() throws Exception {
        when(workOrderService.createWorkOrder(any(WorkOrderDto.class), any(User.class))).thenReturn(1L);

        mockMvc.perform(post(POST_SCHEDULE_SERVICE_PATH)
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("workTypeId", "1")
                .param("bikeId", "1")
                .param("customerDropOffDate", LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .param("customerNotes", "customer notes"))
                .andExpect(model().hasNoErrors())
                .andExpect(status().is3xxRedirection());

        verify(workOrderService, times(1)).createWorkOrder(any(WorkOrderDto.class), any(User.class));
    }

    @WithMockCustomUser
    @Test
    void requestServicePost_invalidInputMissingValues_asAuthenticatedUser_isBadRequest() throws Exception {

        mockMvc.perform(post(POST_SCHEDULE_SERVICE_PATH)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("workTypeId", "1")
                .param("bikeId", "999")
                .param("customerDropOffDate", "") // empty is not valid
                .param("customerNotes", "") // empty not valid
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(workOrderService);
    }

    @WithMockCustomUser
    @Test
    void requestServicePost_invalidInput_asAuthenticatedUser_resultNotFound() throws Exception {
        // this is testing invalid VALUES in the input, but not simple bean validation
        when(workOrderService.createWorkOrder(any(WorkOrderDto.class), any(User.class))).thenThrow(new NotFoundException("bike not found"));

        mockMvc.perform(post(POST_SCHEDULE_SERVICE_PATH)
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("workTypeId", "1")
                .param("bikeId", "999")
                .param("customerDropOffDate", LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .param("customerNotes", "customer notes"))
                .andExpect(status().is4xxClientError());

        verify(workOrderService, times(1)).createWorkOrder(any(WorkOrderDto.class), any(User.class));
    }

    @Test
    void requestServicePost_withValidInput_asAnonymousUser_isOk() throws Exception {
        mockMvc.perform(post(POST_SCHEDULE_SERVICE_PATH)
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("workTypeId", "1")
                .param("bikeId", "1")
                .param("customerDropOffDate", LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .param("customerNotes", "customer notes"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(workOrderService);
    }

    @WithMockUser("authenticatedUser")
    @Test
    void getScheduleServiceWithoutIdPathVariable_asAuthenticatedUser_isRedirectToMainServicesListing() throws Exception {
        // This is NOT an intended valid request path, but would be a simple thing for someone to try and should be handled gracefully
        mockMvc.perform(get(GET_SCHEDULE_SERVICE_PATH))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void getScheduleServiceWithoutIdPathVariable_asUnauthenticatedUser_isUnauthorized() throws Exception {
        // This is NOT an intended valid request path, but would be a simple thing for someone to try and should be handled gracefully
        mockMvc.perform(get(GET_SCHEDULE_SERVICE_PATH))
                .andExpect(status().isUnauthorized());
    }

}
