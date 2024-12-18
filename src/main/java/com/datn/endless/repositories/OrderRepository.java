package com.datn.endless.repositories;

import com.datn.endless.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface OrderRepository extends JpaRepository<Order, String> {
    @Query("SELECT o FROM Order o " +
            "WHERE (:keywords IS NULL OR :keywords = '' OR " +
            "o.orderID = :keywords OR " +
            "o.userID.username LIKE CONCAT('%', :keywords, '%') OR " +
            "o.userID.email LIKE CONCAT('%', :keywords, '%') OR " +
            "o.userID.fullname LIKE CONCAT('%', :keywords, '%') OR " +
            "o.orderAddress LIKE CONCAT('%', :keywords, '%') OR " +
            "o.orderPhone LIKE CONCAT('%', :keywords, '%') OR " +
            "o.orderName LIKE CONCAT('%', :keywords, '%')" +
            ") AND " +
            "( :statusID IS NULL OR " +
            "o.orderID IN (" +
            "   SELECT os.order.orderID FROM Orderstatus os " +
            "   WHERE os.time = (" +
            "       SELECT MAX(os2.time) FROM Orderstatus os2 WHERE os2.order.orderID = os.order.orderID" +
            "   ) AND os.statusType.id = :statusID" +
            ")" +
            ") " +
            "ORDER BY o.orderDate DESC")
    Page<Order> findAllByFilters(
            @Param("keywords") String keywords,
            @Param("statusID") Integer statusID,
            Pageable pageable);


    @Query("select o from Order o " +
            " join Orderdetail od on od.orderID.orderID = o.orderID" +
            " where o.userID.username = :username and (" +
            "   (:keyword is null or od.productVersionID.productID.name like %:keyword%)" +
            "   or (:keyword is null or od.productVersionID.versionName like %:keyword%)" +
            "   or (:keyword is null or o.orderID = :keyword)" +
            ")")
    List<Order> findByUserID_UsernameAndKeyword(@Param("username") String username, @Param("keyword") String keyword);


    List<Order> findByUserID_Username(String username);

    List<Order> findByUserID_UsernameOrderByOrderDateDesc(String username);
}