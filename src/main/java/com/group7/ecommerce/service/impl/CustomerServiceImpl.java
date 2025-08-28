package com.group7.ecommerce.service.impl;

import com.group7.ecommerce.dto.response.CustomerResponse;
import com.group7.ecommerce.enums.Role;
import com.group7.ecommerce.mapper.CustomerMapper;
import com.group7.ecommerce.repository.UserRepository;
import com.group7.ecommerce.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final UserRepository userRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAllPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return userRepository.findAllByRoleName(Role.USER ,pageable)
                .map(customerMapper::toCustomerResponse);
    }
}
