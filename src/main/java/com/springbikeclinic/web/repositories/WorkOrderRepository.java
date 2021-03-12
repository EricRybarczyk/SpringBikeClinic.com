package com.springbikeclinic.web.repositories;

import com.springbikeclinic.web.domain.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

}
