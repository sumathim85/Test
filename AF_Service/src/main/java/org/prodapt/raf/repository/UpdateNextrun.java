package org.prodapt.raf.repository;

import org.prodapt.raf.model.Scheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UpdateNextrun extends JpaRepository<Scheduler,String> {
    @Transactional
    @Modifying
    @Query(value="update scheduler s set s.next_run = :next_run where s.scheduler_id =:scheduler_id",nativeQuery = true)
    void updateNextRun(@Param("scheduler_id") Integer scheduler_id, @Param("next_run") String next_run);
}
