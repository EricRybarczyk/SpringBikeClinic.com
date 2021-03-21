package com.springbikeclinic.web.controllers;

import com.springbikeclinic.web.TestData;
import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.dto.ServiceHistoryItem;
import com.springbikeclinic.web.security.WithMockCustomUser;
import com.springbikeclinic.web.services.WorkOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ServiceHistoryController.class)
@ExtendWith(MockitoExtension.class)
class ServiceHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkOrderService workOrderService;

    // not used directly, but required by Spring Security for mock user authentication
    @MockBean
    private UserDetailsManager userDetailsManager;

    private static final String GET_SERVICE_HISTORY_BASE_PATH = "/account/history";
    private static final String EXPECTED_SERVICE_HISTORY_VIEW_NAME = "account/history";
    private static final String MODEL_ATTRIBUTE_SERVICE_HISTORY = "serviceHistoryList";

    @WithMockCustomUser
    @Test
    void getAccountHistory_asAuthenticatedUser_resultOkAndDataIsPresent() throws Exception {
        when(workOrderService.getServiceHistory(any(User.class))).thenReturn(getMockServiceHistoryItems());

        mockMvc.perform(get(GET_SERVICE_HISTORY_BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(MODEL_ATTRIBUTE_SERVICE_HISTORY))
                .andExpect(view().name(EXPECTED_SERVICE_HISTORY_VIEW_NAME));
    }

    @Test
    void getAccountHistory_asAnonymousUser_isUnauthorized() throws Exception {
        mockMvc.perform(get(GET_SERVICE_HISTORY_BASE_PATH))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(workOrderService);
    }

    private List<ServiceHistoryItem> getMockServiceHistoryItems() {
        List<ServiceHistoryItem> result = new ArrayList<>();
        result.add(
                ServiceHistoryItem.builder()
                        .id(1L)
                        .serviceDate(LocalDate.now().minusDays(3))
                        .bikeDto(TestData.getExistingBikeDto())
                        .serviceDescription("Service")
                        .status("IN-PROCESS")
                        .build()
        );
        result.add(
                ServiceHistoryItem.builder()
                        .id(2L)
                        .serviceDate(LocalDate.now().minusDays(90))
                        .bikeDto(TestData.getExistingBikeDto())
                        .serviceDescription("Service")
                        .status("COMPLETE")
                        .build()
        );
        return result;
    }

}
