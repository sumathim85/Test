package org.prodapt.raf.repository;

import org.prodapt.raf.model.BotDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BotRepository extends JpaRepository<BotDetails, Integer> {
}