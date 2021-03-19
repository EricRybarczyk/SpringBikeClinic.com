package com.springbikeclinic.web.services;

import com.springbikeclinic.web.domain.WorkType;
import com.springbikeclinic.web.exceptions.NotFoundException;
import com.springbikeclinic.web.repositories.WorkTypeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WorkTypeServiceImpl implements WorkTypeService {

    private final WorkTypeRepository workTypeRepository;

    public WorkTypeServiceImpl(WorkTypeRepository workTypeRepository) {
        this.workTypeRepository = workTypeRepository;
    }

    @Override
    public WorkType getWorkType(Long id) {
        return workTypeRepository.findById(id).orElseThrow(() -> new NotFoundException("Requested Service was not found"));
    }

    @Override
    public List<WorkType> listWorkTypes() {
        return workTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "sortPriority"));
    }

}
