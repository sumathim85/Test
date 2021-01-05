package org.prodapt.raf.repository;

import org.prodapt.raf.model.SubOrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubOrderRepository extends JpaRepository<SubOrderDetails, Integer> {
    List<SubOrderDetails> findByOrderId(Integer orderId);
}
