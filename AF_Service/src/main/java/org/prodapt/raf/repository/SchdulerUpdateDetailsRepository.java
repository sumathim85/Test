package org.prodapt.raf.repository;



import org.prodapt.raf.model.Scheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SchdulerUpdateDetailsRepository extends JpaRepository<Scheduler,String> {
    @Transactional
    @Modifying
    @Query(value="update scheduler s set s.job_status = :job_status where s.scheduler_id =:scheduler_id",nativeQuery = true)
    void updateScheduler(@Param("scheduler_id") Integer scheduler_id,@Param("job_status") String job_status);
}
