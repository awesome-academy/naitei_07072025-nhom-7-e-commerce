package com.group7.ecommerce.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.group7.ecommerce.entity.ShipInfo;

public interface ShipInfoRepository extends JpaRepository<ShipInfo, Integer> {

}
