    package com.datn.endless.repositories;

    import com.datn.endless.entities.Order;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;
    import org.springframework.stereotype.Repository;

    import java.math.BigDecimal;
    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.Optional;

    @Repository
    public interface RevenueRepository extends JpaRepository<Order, Integer> {

        @Query("SELECT SUM(od.quantity * od.price) FROM Order o " +
                "JOIN o.orderdetails od " +
                "WHERE o.orderDate >= :startDate AND o.orderDate < :endDate " +
                "AND EXISTS (" +
                "   SELECT 1 FROM Orderstatus os " +
                "   JOIN os.statusType ost " +
                "   WHERE os.order = o AND ost.name = 'Đã giao hàng'" +
                ")")
        Optional<BigDecimal> calculateTotalRevenue(@Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);

        @Query("SELECT MONTH(o.orderDate) AS month, SUM(od.quantity * od.price) AS totalRevenue " +
                "FROM Order o " +
                "JOIN o.orderdetails od " +
                "WHERE o.orderDate >= :startDate AND o.orderDate < :endDate " +
                "AND EXISTS (" +
                "   SELECT 1 FROM Orderstatus os " +
                "   JOIN os.statusType ost " +
                "   WHERE os.order = o AND ost.name = 'Đã giao hàng'" +
                ") " +
                "GROUP BY MONTH(o.orderDate) " +
                "ORDER BY month")
        List<Object[]> calculateRevenueByMonth(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);
    }
