package com.springbikeclinic.web.controllers;

import com.springbikeclinic.web.domain.security.SecurityUser;
import com.springbikeclinic.web.dto.ServiceHistoryItem;
import com.springbikeclinic.web.dto.WorkOrderDto;
import com.springbikeclinic.web.services.WorkOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("account/history")
@RequiredArgsConstructor
@Slf4j
public class ServiceHistoryController {

    private final WorkOrderService workOrderService;

    private static final String MODEL_ATTRIBUTE_SERVICE_HISTORY = "serviceHistoryList";
    private static final String MODEL_ATTRIBUTE_WORK_ORDER = "workOrderDto";


    @GetMapping()
    public String accountHistory(Model model, Principal principal) {
        final List<ServiceHistoryItem> serviceHistory = workOrderService.getServiceHistory(SecurityUser.from(principal).getUser());

        model.addAttribute(MODEL_ATTRIBUTE_SERVICE_HISTORY, serviceHistory);
        return "account/history";
    }

    @GetMapping("/detail/{workOrderId}")
    public String getWorkOrderDetails(@PathVariable("workOrderId") Long workOrderId, Model model, Principal principal) {
        final WorkOrderDto workOrderDto = workOrderService.getWorkOrder(workOrderId, SecurityUser.from(principal).getUser());

        model.addAttribute(MODEL_ATTRIBUTE_WORK_ORDER, workOrderDto);
        return "account/workOrder";
    }
}
