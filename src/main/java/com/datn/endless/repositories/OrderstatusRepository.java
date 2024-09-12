package com.datn.endless.repositories;

import com.datn.endless.entities.Orderstatus;
import com.datn.endless.entities.OrderstatusId;
import com.datn.endless.entities.Orderstatustype;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderstatusRepository extends JpaRepository<Orderstatus, OrderstatusId> {
    @Query("SELECT o FROM Orderstatustype o WHERE o.name = :name")
    Optional<Orderstatustype> findByName(@Param("name") String name);

    @Query("SELECT o FROM Orderstatus o WHERE o.id.orderID = :orderID AND o.id.statusID = :statusID")
    Optional<Orderstatus> findByOrderIDAndStatusID(@Param("orderID") String orderID, @Param("statusID") Integer statusID);

    List<Orderstatus> findByOrder_OrderID(String orderID);
}