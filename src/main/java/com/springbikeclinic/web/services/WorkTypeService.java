package com.springbikeclinic.web.services;

import com.springbikeclinic.web.domain.WorkType;
import java.util.List;

public interface WorkTypeService {
    WorkType getWorkType(Long id);
    List<WorkType> listWorkTypes();
}
