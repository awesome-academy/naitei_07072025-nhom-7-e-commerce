package com.group7.ecommerce.repository;

import com.group7.ecommerce.entity.ShipInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipInfoRepository extends JpaRepository<ShipInfo, Integer> {

    Optional<ShipInfo> findByUserIdAndIsDefaultTrue(Integer userId);
    List<ShipInfo> findByUserIdOrderByIdDesc(Integer userId);
    List<ShipInfo> findByUserId(Integer userId);
    Optional<ShipInfo> findByUserIdAndId(Integer userId,Integer shipInfoId);
    List<ShipInfo> findByUserIdAndIsDeletedFalse(Integer userId);

    @Modifying
    @Query("UPDATE ShipInfo s SET s.isDefault = false WHERE s.user.id = :userId AND s.isDefault = true")
    void updateAllToNotDefault(@Param("userId") Integer userId);

    boolean existsByUserIdAndId(Integer userId, Integer id);
}
