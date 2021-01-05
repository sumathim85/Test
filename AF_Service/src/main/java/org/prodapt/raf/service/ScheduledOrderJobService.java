package org.prodapt.raf.service;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONException;
import org.prodapt.raf.controller.RafController;
import org.prodapt.raf.model.Scheduler;
import org.prodapt.raf.repository.SchdulerDetailsRepository;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Component
public class ScheduledOrderJobService extends QuartzJobBean  {

    Logger logger = LoggerFactory.getLogger(ScheduledOrderJobService.class);
    @Autowired
    private RafController rafController;

    @Autowired
    SchdulerDetailsRepository schedulerDetailsRepository;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.debug("Executing Scheduled Job order : {}", jobExecutionContext.getJobDetail().getKey());
        List<Scheduler>  schedulerList = schedulerDetailsRepository.findAllByJobId(jobExecutionContext.getJobDetail().getKey().getName());
        if (!schedulerList.isEmpty()) {
            System.out.println(schedulerList.get(0).getSchedulerId());
            try {
                rafController.createScheduledOrder(schedulerList.get(0).getSchedulerId());
            } catch (IOException | JSONException | ParseException | UnirestException e) {
                e.printStackTrace();
            }
        }
    }
}