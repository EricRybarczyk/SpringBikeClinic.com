package com.springbikeclinic.web.repositories;

import com.springbikeclinic.web.domain.WorkOrder;
import com.springbikeclinic.web.domain.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    List<WorkOrder> findAllByUser(User user);
}
