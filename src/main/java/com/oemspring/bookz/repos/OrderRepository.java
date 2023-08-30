package com.oemspring.bookz.repos;

import com.oemspring.bookz.models.Order;
import com.oemspring.bookz.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    ///ELECT  * from spring.orders o  WHERE o.order_status  like 'DELIVERED' and o.touchedtime  >= NOW() - INTERVAL 1 DAY
    @Query(
            value = "SELECT  * from spring.orders o  WHERE o.order_status  like 'DELIVERED' and o.touchedtime  >= NOW() - INTERVAL 1 DAY",
            nativeQuery = true)
    Collection<Order> findAllDeliveredAgedOne();
}
