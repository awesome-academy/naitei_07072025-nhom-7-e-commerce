package com.group7.ecommerce.service;

import com.group7.ecommerce.dto.response.CustomerResponse;
import org.springframework.data.domain.Page;

public interface CustomerService {

    Page<CustomerResponse> getAllPaged(int page, int size);
}
