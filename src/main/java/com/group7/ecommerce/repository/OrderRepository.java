package com.group7.ecommerce.repository;

import com.group7.ecommerce.entity.ShipInfo;
import com.group7.ecommerce.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
	@Query("""
			SELECT o FROM Order o
			JOIN FETCH o.user u
			JOIN FETCH o.shipInfo s
			LEFT JOIN FETCH o.orderItems oi
			LEFT JOIN FETCH oi.product p
			LEFT JOIN FETCH o.reason r
			WHERE o.id = :id
			""")
	Optional<Order> findDetailsById(@Param("id") Integer id);
    List<Order> findByUserIdAndShipInfo(Integer userId, ShipInfo shipInfo);
}
