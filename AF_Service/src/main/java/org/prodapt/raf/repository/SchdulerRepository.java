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
public interface SchdulerRepository extends JpaRepository<Scheduler, Integer> {
    @Transactional
    @Modifying
    @Query(value="update scheduler s set s.job_status = 'true' where s.scheduler_id =:scheduler_id",nativeQuery = true)
    void updateScheduler(@Param("scheduler_id") Integer scheduler_id);
//    @Query(value="delete from scheduler where scheduler_id =:scheduler_id",nativeQuery = true)
//    void delete(@Param("scheduler_id") Integer scheduler_id);

}
