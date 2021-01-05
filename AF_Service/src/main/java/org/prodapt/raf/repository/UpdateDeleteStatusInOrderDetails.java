package org.prodapt.raf.repository;

import org.prodapt.raf.model.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UpdateDeleteStatusInOrderDetails extends JpaRepository<OrderDetails,String> {
    @Transactional
    @Modifying
    @Query(value="update order_details s set s.is_scheduled = :is_scheduled where s.scheduler_id =:scheduler_id",nativeQuery = true)
    void updateDeleteInOrderDetails(@Param("is_scheduled") String is_scheduled, @Param("scheduler_id") Integer scheduler_id);
}
