package org.prodapt.raf.repository;

import org.prodapt.raf.model.Scheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UpdateScheduleDetails extends JpaRepository<Scheduler,String> {
    @Transactional
    @Modifying
        @Query(value="update scheduler s set s.month = :month,s.day_of_week = :day_of_week,s.day = :day,s.day = :day,s.minute = :minute,s.hour = :hour,s.request_body = :request_body where s.scheduler_id =:scheduler_id",nativeQuery = true)
//    @Query(value="update scheduler s set s.month = 12 where s.scheduler_id =7",nativeQuery = true)
    void updateSchedule(@Param("scheduler_id") Integer scheduler_id, @Param("month") String month,@Param("day_of_week") String day_of_week,@Param("day") String day,@Param("minute") String minute,@Param("hour") String hour,@Param("request_body") String request_body);

}
