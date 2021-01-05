//package org.prodapt.raf.repository;
//
//import org.prodapt.raf.model.SchdulerDetails;
//import org.prodapt.raf.model.Scheduler;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.transaction.annotation.Transactional;
//
//public interface UpdateJobId  extends JpaRepository<Scheduler,String> {
//    @Transactional
//    @Modifying
////    @Query (value = "UPDATE schduler_details SET job_id=?2 WHERE scheduler_id = ?1", nativeQuery = true)
//    @Query(value="update scheduler s set s.job_id = :job_id where s.scheduler_id =:scheduler_id",nativeQuery = true)
//    void updatejobid(@Param("scheduler_id") Integer scheduler_id, @Param("job_id") String job_id);
//}

package org.prodapt.raf.repository;


import org.prodapt.raf.model.Scheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface UpdateJobIdRepository extends JpaRepository<Scheduler,String> {
    @Transactional
    @Modifying
    @Query(value="update scheduler s set s.job_id = :job_id where s.scheduler_id =:scheduler_id",nativeQuery = true)
    void updatejobid(@Param("scheduler_id") Integer scheduler_id, @Param("job_id") String job_id);
}
