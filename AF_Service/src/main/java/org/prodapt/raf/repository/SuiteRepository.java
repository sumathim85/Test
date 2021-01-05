package org.prodapt.raf.repository;


import org.prodapt.raf.model.SuiteDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuiteRepository extends JpaRepository<SuiteDetails, Integer>{
    List<SuiteDetails> findByOrderIdAndSubOrderId(Integer orderId, Integer subOrderId);
}