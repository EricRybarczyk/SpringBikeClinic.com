package com.springbikeclinic.web.mappers;

import com.springbikeclinic.web.domain.Customer;
import com.springbikeclinic.web.dto.CustomerDto;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {

    Customer customerDtoToCustomer(CustomerDto customerDto);

    CustomerDto customerToCustomerDto(Customer customer);

}
