package com.springbikeclinic.web.repositories;

import com.springbikeclinic.web.domain.Bike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BikeRepository extends JpaRepository<Bike, Long> {

}
