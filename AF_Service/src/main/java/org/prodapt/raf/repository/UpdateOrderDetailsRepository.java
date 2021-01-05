package org.prodapt.raf.repository;

import org.prodapt.raf.model.OrderDetails;
import org.prodapt.raf.model.Scheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UpdateOrderDetailsRepository extends JpaRepository<OrderDetails,String> {
    @Transactional
    @Modifying
    @Query(value="update order_details s set s.is_scheduled = :is_scheduled, s.scheduler_id = :scheduler_id, s.scheduler_name = :scheduler_name where s.order_id =:order_id",nativeQuery = true)
    void updateOrder(@Param("is_scheduled") String is_scheduled,@Param("scheduler_id") Integer scheduler_id,@Param("order_id") Integer order_id,@Param("scheduler_name") String scheduler_name);
}
