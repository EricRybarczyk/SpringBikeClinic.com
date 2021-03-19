package com.springbikeclinic.web.controllers;

import com.springbikeclinic.web.TestData;
import com.springbikeclinic.web.domain.WorkType;
import com.springbikeclinic.web.security.WithMockCustomUser;
import com.springbikeclinic.web.services.BikeService;
import com.springbikeclinic.web.services.WorkOrderService;
import com.springbikeclinic.web.services.WorkTypeServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServicesController.class)
class ServicesControllerTest {

    private static final String GET_SERVICES_PATH = "/services";
    private static final String GET_SCHEDULE_SERVICE_PATH = "/services/schedule";
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
