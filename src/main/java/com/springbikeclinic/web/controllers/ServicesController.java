package com.springbikeclinic.web.controllers;

import com.springbikeclinic.web.domain.WorkType;
import com.springbikeclinic.web.domain.security.SecurityUser;
import com.springbikeclinic.web.dto.BikeDto;
import com.springbikeclinic.web.dto.WorkOrderDto;
import com.springbikeclinic.web.services.BikeService;
import com.springbikeclinic.web.services.WorkTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("services")
@RequiredArgsConstructor
public class ServicesController {

    private final WorkTypeService workTypeService;
    private final BikeService bikeService;

    private static final String MODEL_ATTRIBUTE_WORK_ORDER = "workOrderDto";
    private static final String MODEL_ATTRIBUTE_WORK_TYPE = "workType";
    private static final String MODEL_ATTRIBUTE_BIKE_LIST = "bikeList";

    @RequestMapping
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

    @RequestMapping("/schedule/{workTypeId}")
    public String scheduleService(Model model, @PathVariable Long workTypeId, Principal principal) {
        final Long userId = SecurityUser.from(principal).getUser().getId();
        final WorkType workType = workTypeService.getWorkType(workTypeId);
        final List<BikeDto> bikes = bikeService.getBikes(userId);

        // sort by year since year will be shown first in the drop-down items in the View so this "feels right"
        bikes.sort(Comparator.comparingInt(BikeDto::getModelYear));

        model.addAttribute(MODEL_ATTRIBUTE_WORK_ORDER, new WorkOrderDto());
        model.addAttribute(MODEL_ATTRIBUTE_WORK_TYPE, workType);
        model.addAttribute(MODEL_ATTRIBUTE_BIKE_LIST, bikes);

        return "scheduleService";
    }
}
