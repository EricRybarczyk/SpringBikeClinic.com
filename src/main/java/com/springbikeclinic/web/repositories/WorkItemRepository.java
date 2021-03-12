package com.springbikeclinic.web.repositories;

import com.springbikeclinic.web.domain.WorkItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkItemRepository extends JpaRepository<WorkItem, Long> {

}
