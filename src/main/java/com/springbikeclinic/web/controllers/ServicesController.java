package com.springbikeclinic.web.controllers;

import com.springbikeclinic.web.domain.WorkType;
import com.springbikeclinic.web.services.WorkTypeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
public class ServicesController {

    private final WorkTypeService workTypeService;

    public ServicesController(WorkTypeService workTypeService) {
        this.workTypeService = workTypeService;
    }

    @RequestMapping("/services")
    public String services(Model model) {
        final List<WorkType> workTypes = workTypeService.listWorkTypes();
        model.addAttribute("workTypeList", workTypes);
        return "services";
    }

    // Handle a potential url mistake of missing {workTypeId} path var by redirecting gracefully.
    // This is NOT an intended valid request path, but would be a simple thing for someone to try.
    // Note this is still a protected resource so non-authenticated user will get a 401 first.
    @RequestMapping("/services/schedule")
    public String services() {
        return "redirect:/services";
    }

    @RequestMapping("/services/schedule/{workTypeId}")
    public String scheduleService(Model model, @PathVariable Long workTypeId) {
        final WorkType workType = workTypeService.getWorkType(workTypeId);
        model.addAttribute("workType", workType);
        return "scheduleService";
    }
}
