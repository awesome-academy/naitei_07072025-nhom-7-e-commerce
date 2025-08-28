package com.group7.ecommerce.mapper;

import com.group7.ecommerce.dto.response.CustomerResponse;
import com.group7.ecommerce.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerResponse toCustomerResponse(User user);
}
