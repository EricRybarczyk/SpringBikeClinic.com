package com.springbikeclinic.web.repositories;

import com.springbikeclinic.web.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}

