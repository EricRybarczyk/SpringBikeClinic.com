package com.springbikeclinic.web.controllers;

import com.springbikeclinic.web.domain.WorkType;
import com.springbikeclinic.web.domain.security.SecurityUser;
import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.dto.BikeDto;
import com.springbikeclinic.web.dto.WorkOrderDto;
import com.springbikeclinic.web.services.BikeService;
import com.springbikeclinic.web.services.WorkOrderService;
import com.springbikeclinic.web.services.WorkTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("services")
@RequiredArgsConstructor
@Slf4j
public class ServicesController {

    private final WorkTypeService workTypeService;
    private final BikeService bikeService;
    private final WorkOrderService workOrderService;

    private static final String MODEL_ATTRIBUTE_WORK_ORDER = "workOrderDto";
    private static final String MODEL_ATTRIBUTE_WORK_TYPE = "workType";
    private static final String MODEL_ATTRIBUTE_BIKE_LIST = "bikeList";

    @GetMapping
    public String services(Model model) {
        final List<WorkType> workTypes = workTypeService.listWorkTypes();
        model.addAttribute("workTypeList", workTypes);
        return "services";
    }

    // Handle a potential url mistake of missing {workTypeId} path var by redirecting gracefully.
    // This is NOT an intended valid request path, but would be a simple thing for someone to try.
    // Note this is still a protected resource so non-authenticated user will get a 401 first.
    @RequestMapping("/schedule")
    public String services() {
        return "redirect:/services";
    }

    @GetMapping("/schedule/{workTypeId}")
    public String scheduleService(Model model, @PathVariable Long workTypeId, Principal principal) {
        model.addAttribute(MODEL_ATTRIBUTE_WORK_ORDER, new WorkOrderDto());
        model.addAttribute(MODEL_ATTRIBUTE_WORK_TYPE, workTypeService.getWorkType(workTypeId));
        model.addAttribute(MODEL_ATTRIBUTE_BIKE_LIST, getBikesForUser(SecurityUser.from(principal).getUser()));

        return "scheduleService";
    }

    @PostMapping("/schedule/save")
    public String saveServiceRequest(@ModelAttribute("workOrderDto") @Valid WorkOrderDto workOrderDto, Model model, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> log.debug(e.toString()));

            model.addAttribute(MODEL_ATTRIBUTE_WORK_ORDER, workOrderDto);
            model.addAttribute(MODEL_ATTRIBUTE_WORK_TYPE, workTypeService.getWorkType(workOrderDto.getWorkTypeId()));
            model.addAttribute(MODEL_ATTRIBUTE_BIKE_LIST, getBikesForUser(SecurityUser.from(principal).getUser()));

            return "scheduleService";
        }

        final Long workOrderId = workOrderService.createWorkOrder(workOrderDto, SecurityUser.from(principal).getUser());

        return String.format("redirect:/account/history?w=%s", workOrderId);
    }

    private List<BikeDto> getBikesForUser(User user) {
        final List<BikeDto> bikes = bikeService.getBikes(user.getId());
        // sort by year since year will be shown first in the drop-down items in the View so this "feels right"
        bikes.sort(Comparator.comparingInt(BikeDto::getModelYear));
        return bikes;
    }
}
