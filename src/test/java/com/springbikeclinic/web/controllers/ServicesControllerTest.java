package com.springbikeclinic.web.controllers;

import com.springbikeclinic.web.TestData;
import com.springbikeclinic.web.domain.WorkType;
import com.springbikeclinic.web.services.WorkTypeServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @Test
    void getServices() throws Exception {
        final List<WorkType> workTypes = TestData.getWorkTypesList();
        when(workTypeService.listWorkTypes()).thenReturn(workTypes);

        mockMvc.perform(get(GET_SERVICES_PATH))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("workTypeList"))
                .andExpect(model().attribute("workTypeList", workTypes))
                .andExpect(view().name(EXPECTED_GET_SERVICES_VIEW_NAME));
    }

    @Test
    void beginScheduleService() throws Exception {
        final WorkType workType = TestData.getSingleWorkType();
        when(workTypeService.getWorkType(anyLong())).thenReturn(workType);

        mockMvc.perform(get(GET_SCHEDULE_SERVICE_PATH + "/1")).andExpect(status().isOk())
                .andExpect(model().attributeExists("workType"))
                .andExpect(model().attribute("workType", workType))
                .andExpect(view().name(EXPECTED_GET_SCHEDULE_SERVICE_VIEW_NAME));
    }

}
