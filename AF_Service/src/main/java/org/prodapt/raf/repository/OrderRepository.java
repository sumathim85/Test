package org.prodapt.raf.repository;
import org.prodapt.raf.model.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface OrderRepository extends JpaRepository<OrderDetails, Integer> {
    List<OrderDetails> findByOrderId(Integer orderId);
    List<OrderDetails> findBySchedulerId(Integer schedulerId);
    List<OrderDetails> findByOrderByOrderIdDesc();
    List<OrderDetails> findByOrderCreatedBy(String orderCreatedBy);
    List<OrderDetails> findByTeam(String team);
    List<OrderDetails> findByRoleIn(List<String> role);

}
