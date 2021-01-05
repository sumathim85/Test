package org.prodapt.raf.repository;



import org.prodapt.raf.model.OrderDetails;
import org.prodapt.raf.model.Scheduler;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SchdulerDetailsRepository extends JpaRepository<Scheduler,String> {
    List<Scheduler> findAllByJobStatus(String jobStatus);
    List<Scheduler> findAllBySchedulerId(Integer schedulerId);
    List<Scheduler> findAllByRequestBody(String requestBody);
    List<Scheduler> findByOrderBySchedulerIdDesc();
    List<Scheduler> findAllByJobId(String jobId);
    List<Scheduler> findByRoleIn(List<String> role);
    Scheduler findAllByJobname(String jobname);
}
