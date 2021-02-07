package com.springbikeclinic.web.repositories;

import com.springbikeclinic.web.domain.WorkType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkTypeRepository extends JpaRepository<WorkType, Long> {
}
