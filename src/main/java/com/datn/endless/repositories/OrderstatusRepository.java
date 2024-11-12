package com.datn.endless.repositories;

import com.datn.endless.entities.Orderstatus;
import com.datn.endless.entities.OrderstatusId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderstatusRepository extends JpaRepository<Orderstatus, OrderstatusId> {
    List<Orderstatus> findByOrder_OrderID(String orderID);

    @Query(value = "SELECT o FROM Orderstatus o WHERE o.order.orderID = :orderID ORDER BY o.statusType.id DESC LIMIT 1")
    Optional<Orderstatus> findTopByOrderIdOrderByTimeDesc(@Param("orderID") String orderID);
}