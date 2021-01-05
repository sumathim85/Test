package org.prodapt.raf.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.prodapt.raf.exception.OrderDetailsNotfoundException;
import org.prodapt.raf.model.*;
import org.prodapt.raf.model.Scheduler;
import org.prodapt.raf.repository.*;
import org.prodapt.raf.robot.RobotExecution;
import org.prodapt.raf.robot.FlowExecution;
import org.prodapt.raf.service.RafService;
import org.prodapt.raf.service.ScheduledOrderJobService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Null;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import springfox.documentation.spring.web.json.Json;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StreamUtils;
import javax.servlet.http.HttpServletResponse;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/order")
@Configuration
public class RafController {
    @Value("${file.path}")
    private String pathForAccess;
    @Value("${node-red.ip}")
    private String noderedIP;
    @Value("${elastic-ip}")
    private String elasticIP;
    @Value("${elastic-port}")
    private Integer elasticPort;
    Logger logger = LoggerFactory.getLogger(RafController.class);
    @Autowired
    UpdateOrderDetailsRepository updateOrderDetailsRepository;
    @Autowired
    UpdateDeleteStatusInOrderDetails updateDeleteStatusInOrderDetails;
    @Autowired
    UpdateScheduleDetails updateScheduleDetails;
    @Autowired
    SchdulerUpdateDetailsRepository schedulerUpdateDetailsRepository;
    @Autowired
    SchdulerDetailsRepository schedulerDetailsRepository;
    @Autowired
    UpdateJobIdRepository updateJobIdRepository;
    @Autowired
    SchdulerRepository schedulerRepository;
    @Autowired
    UpdateNextrun updateNextrun;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    BotRepository botRepository;
    @Autowired
    RafService rafService;
    @Autowired
    SubOrderRepository subOrderRepository;
    @Autowired
    org.quartz.Scheduler quartzScheduler;

    @Value("${raf-create-order-uri}")
    private String createOrderURI;
    @Value("${raf-create-rerun-uri}")
    private String createRerunURI;
    @Value("${raf-service-ip}")
    private String rafServiceIP;
    @Value("${raf-service-port}")
    private String rafServicePort;

    @Value("${spring.datasource.url}")
    private String dataBaseUrl;

    @GetMapping("/")
    public String hello(@RequestParam(value = "name", defaultValue = "TEST OK !!") String name){
        return String.format("Prodapt RAF Engine check %s!", name);
    }


    @CrossOrigin(origins = "*")
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderDetails orderDetails) throws IOException, JSONException {

        if(orderDetails.getOrderCreatedBy()==null)
            throw new OrderDetailsNotfoundException("Order created by");
        if (orderDetails.getOrderType()==null)
            throw new OrderDetailsNotfoundException("Order type");
        if (orderDetails.getSuiteDetails()==null)
            throw new OrderDetailsNotfoundException("Suite details");
        if (orderDetails.getConfType() == null)
            throw new OrderDetailsNotfoundException("Conf type");


        boolean rerun = false;

        Integer orderid = rafService.addOrderDetails(orderDetails);
        if (orderDetails.getServiceType().equals("workflow")){
            rafService.updateFlowDeails(orderDetails,orderid);

            if(orderDetails.getFlowDetails().get(0).getTriggerType().toLowerCase().equals("api")){
                JSONObject jsonInputString = new JSONObject();
                jsonInputString.put("Id",orderid);

                String noderedurl= noderedIP + orderDetails.getFlowDetails().get(0).getFlowName().toLowerCase();

                FlowExecution flowExecution=new FlowExecution(jsonInputString,noderedurl);
                Thread robotExecute= new Thread(flowExecution);
                robotExecute.start();
            }

        }
        else{
            rafService.updateSuiteDetails(orderDetails,orderid,orderDetails.getSubOrderId());
            rafService.updateBotDetails(orderDetails,orderid,orderDetails.getSubOrderId());

            String folderPath = pathForAccess + "RobotLogs/" + orderid;

            rafService.folderCreation(orderDetails,folderPath);
            rafService.testPlanCreation(orderDetails,folderPath);
            rafService.logToDashboard("Order Processing is Done by RAF Engine , Order Successfully Transferred to Bots","INFO",orderid);
            RobotExecution robotExecution=new RobotExecution(orderDetails,pathForAccess,orderid,folderPath,rerun,orderDetails.getSubOrderId(),rafService,elasticIP,elasticPort,dataBaseUrl);
            Thread robotExecute= new Thread(robotExecution);
            robotExecute.start();
            rafService.logToDashboard("Order Successfully Received By Bots for Execution","INFO",orderid);
        }
        JSONObject entity = new JSONObject();
        entity.put("orderid",orderid);

        return new ResponseEntity<Object>(entity.toString(), HttpStatus.valueOf(201));
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getInfoByOrderId")
    public List<OrderDetails> getOrder(@RequestParam Integer orderId) {
        return orderRepository.findByOrderId(orderId);
    }


    @CrossOrigin(origins = "*")
    @GetMapping(value="/getOrderList")
    public List<OrderDetails> getBotSummary(@RequestParam List<String> role)  {
        List<OrderDetails> orderDetails=orderRepository.findByRoleIn(role);
        Collections.reverse(orderDetails);
        return orderDetails;
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value="/getOrderListByUser")
    public List<OrderDetails> getBotSummarybyusername(@RequestParam String orderCreatedBy) {
        return orderRepository.findByOrderCreatedBy(orderCreatedBy);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value="/getOrderListByTeam")
    public List<OrderDetails> getBotSummarybyteam(@RequestParam String team) {
        return orderRepository.findByTeam(team);
    }


    @CrossOrigin(origins = "*")
    @PostMapping("/rerun")
    public ResponseEntity<?> orderRerun(@RequestBody SubOrderDetails subOrderDetails) throws IOException, JSONException {

        if(subOrderDetails.getOrderCreatedBy()==null)
            throw new OrderDetailsNotfoundException("Order created by");
        if (subOrderDetails.getOrderType()==null)
            throw new OrderDetailsNotfoundException("Order type");
        if (subOrderDetails.getSuiteDetails()==null)
            throw new OrderDetailsNotfoundException("Suite details");
        if (subOrderDetails.getConfType() == null)
            throw new OrderDetailsNotfoundException("Conf type");
        if (subOrderDetails.getServiceType() == null)
            throw new OrderDetailsNotfoundException("Service type");
        for (int i = 0; i < subOrderDetails.getSuiteDetails().size(); i++) {
            for (int j=0 ;j< subOrderDetails.getSuiteDetails().get(i).getBotDetails().size();j++){

                if(subOrderDetails.getSuiteDetails().get(i).getBotDetails().get(j).isRollback() == null){
                    throw new OrderDetailsNotfoundException("rollback details");
                }

            }
        }

        Integer orderid = subOrderDetails.getOrderId();
        List list = orderRepository.findByOrderId(orderid);
        if (list.size() == 0)
            throw new OrderDetailsNotfoundException("Order Id");

        Integer suborderid;
        List<Integer> suborderlist = new ArrayList<>();
        for (int i =0;i<subOrderRepository.findByOrderId(orderid).size();i++) {
            Integer subid = subOrderRepository.findByOrderId(orderid).get(i).getSubOrderId();
            suborderlist.add(subid);
        }
        if(suborderlist.size() == 0){
            suborderid = 1;
        } else{
            Integer len = suborderlist.size() -1;
            suborderid = suborderlist.get(len) +1;
        }
        Integer counter = 0;

        for (int i = 0; i < subOrderDetails.getSuiteDetails().size(); i++) {
            Integer yesCounter = 0;
            for (int j = 0; j < subOrderDetails.getSuiteDetails().get(i).getBotDetails().size(); j++) {
                String deviceJsonObject = subOrderDetails.getSuiteDetails().get(i).getBotDetails().get(j).isRollback();

                if(deviceJsonObject.equals("yes")){
                    counter =+1;
                }
            }
        }

        boolean rerun = true;
        rafService.addSubOrderDetails(subOrderDetails,suborderid);
        rafService.updateSuiteDetailsForSuborder(subOrderDetails,orderid,suborderid);
        rafService.updateBotDetailsForSuborder(subOrderDetails,orderid,suborderid);

        String folderPath = pathForAccess + "RobotLogs/" + orderid + "/" + suborderid;

        rafService.folderCreationForSuborder(subOrderDetails,folderPath,suborderid);

        rafService.testPlanCreationForSuborder(subOrderDetails,folderPath,counter);
//        rafService.logToDashboard("Sub Order Processing is Done by RAF Engine , Order Successfully Transferred to Bots","INFO",orderid);
        RobotExecution robotExecution=new RobotExecution(subOrderDetails,pathForAccess,orderid,folderPath,rerun,suborderid,rafService,elasticIP,elasticPort,dataBaseUrl);
        Thread robotExecute= new Thread(robotExecution);
        robotExecute.start();
//        rafService.logToDashboard("Sub Order Successfully Received By Bots for Execution","INFO",orderid);
        JSONObject entity = new JSONObject();
        entity.put("suborderid",orderid+"_"+suborderid);

        return new ResponseEntity<Object>(entity.toString(), HttpStatus.valueOf(201));
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getReport")
    public ResponseEntity reportInfo(@RequestParam List<Integer> id) throws IOException {
        String fileDownloadUri = null;
        System.out.println(pathForAccess);
        for(int i=0;i< id.size();i++){
            System.out.println(id.get(i));
            String filePath = pathForAccess + "RobotLogs/" + id.get(i) +"/" + "report.html";
            System.out.println(filePath);
            fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/order/downloadReport/report.html?id="+id.get(i))
                    .buildAndExpand("?id=", id.get(i))
                    .toUriString();

        }

        return ResponseEntity.ok(fileDownloadUri);
    }

//     @CrossOrigin(origins = "*")
//     @RequestMapping(value= "/downloadReport/report.html", params = "id", method = RequestMethod.GET)
//     public ResponseEntity<Resource> downloadReport(@RequestParam List<Integer> id) throws IOException{

//         File file = null;
//         ByteArrayResource resource = null;
//         resource=rafService.GetReport(resource,pathForAccess,id,file);
//         return ResponseEntity.ok()
// //          .headers(headers)
// //				.contentLength(file.length())
//                 .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                 .body(resource);
//     }

    @CrossOrigin(origins = "*")
    @RequestMapping(value= "/viewReport/report.html", params = "id", method = RequestMethod.GET)
    public String viewReport(@RequestParam List<Integer> id) throws IOException{

        File file = null;
        ByteArrayResource resource = null;
        String filename= "/report.html";
        String resources = rafService.GetReport(resource, pathForAccess, id, file,filename);

        return resources;
    }

    @CrossOrigin(origins = "*") 
    @RequestMapping(value= "/viewReport/log.html", params = "id", method = RequestMethod.GET)
    public String viewLog(@RequestParam List<Integer> id) throws IOException{

        File file = null;
        ByteArrayResource resource = null;
        String filename= "/log.html";

        String resources = rafService.GetReport(resource, pathForAccess, id, file,filename);

        return resources;
    }
    
    @CrossOrigin(origins = "*")
    @GetMapping(value = "/zip-download", produces="application/zip", params = "id")
    public void zipDownload(@RequestParam List<Integer> id, HttpServletResponse response) throws IOException {
        ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());

        String[] name= new String[]{"report.html", "log.html"};
        for (String fileName : name) {
            for(int i=0;i< id.size();i++) {
                FileSystemResource resource = new FileSystemResource(pathForAccess + "RobotLogs/" + id.get(i) +"/"+ fileName);
                ZipEntry zipEntry = new ZipEntry(resource.getFilename());
                zipEntry.setSize(resource.contentLength());
                zipOut.putNextEntry(zipEntry);
                StreamUtils.copy(resource.getInputStream(), zipOut);
                zipOut.closeEntry();
            }
        }
        zipOut.finish();
        zipOut.close();
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipOut + "\"");
    }




    @CrossOrigin(origins = "*")
    @PostMapping("/createSchduler")
    public ResponseEntity<?> createSchduler(@RequestBody Scheduler scheduler) throws IOException, JSONException, ParseException {

            String cronValue, jobId;
            JobDetail jobDetail;
            Trigger trigger;
            Date nextRun, scheduledTime;
            JSONObject entity;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");


            logger.info("Executing quartz Scheduler");
            if (isNullOrEmpty(scheduler.getMinute()) || isNullOrEmpty(scheduler.getMonth()) || isNullOrEmpty(scheduler.getDay_of_week()) || isNullOrEmpty(scheduler.getDay()) || isNullOrEmpty(scheduler.getHour()) == true) {
                logger.error("Given Scheduler Input Is wrong");
                return new ResponseEntity<Object>("Invalid schedule input", HttpStatus.valueOf(400));
            }
            if (isNullOrEmpty(scheduler.getJobname())) {
                logger.error("Job name should not be null or empty");
                return new ResponseEntity<Object>("Invalid jobname", HttpStatus.valueOf(400));
            }
            Scheduler jobname = schedulerDetailsRepository.findAllByJobname(scheduler.getJobname());
            if (jobname != null) {
                logger.error("Job Name Already Exists");
                return new ResponseEntity<Object>("Job Name Already Exists", HttpStatus.valueOf(400));
            }
            cronValue = rafService.buildCronValue(scheduler);
            if (!CronExpression.isValidExpression(cronValue)) {
                logger.error("Invalid scheduling cron time inputs");
                return new ResponseEntity<Object>("Invalid schedule input", HttpStatus.valueOf(400));
            }
            logger.info("Valid scheduling cron time : {}",cronValue);
            //JOB CREATION
            jobDetail = rafService.buildJobDetail(scheduler);
            if (jobDetail != null && jobDetail.getKey() != null && !isNullOrEmpty(jobDetail.getKey().getName())) {
                jobId = jobDetail.getKey().getName();
            } else {
                logger.error("JobID should not be null or empty");
                return new ResponseEntity<Object>("create schedule failed", HttpStatus.valueOf(500));
            }
            //TRIGGER CREATION FOR JOB
            trigger = rafService.buildJobTrigger(jobDetail, cronValue, scheduler);
            if (trigger != null && trigger.getKey() != null) {
                logger.trace("JobId: {} TriggerId: {} ", jobId, trigger.getKey());
                try {
                    scheduledTime = quartzScheduler.scheduleJob(jobDetail, trigger);
                    nextRun = trigger.getNextFireTime();
                    dateFormat.applyPattern("yyyy-MM-dd HH:mm");
                    logger.trace("NextRun : {}", dateFormat.format(nextRun));
                } catch (SchedulerException ex) {
                    return new ResponseEntity<Object>("create schedule failed :  ", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                logger.error("JobTrigger should not be null or empty");
                return new ResponseEntity<Object>("create schedule failed", HttpStatus.valueOf(500));
            }
            scheduler.setJobId(jobId);
            scheduler.setNextRun(dateFormat.format(nextRun));
            scheduler.setJobStatus("Active");
            rafService.addSchdulerDetails(scheduler);
            entity = new JSONObject();
            entity.put("scheduler_id",scheduler.getSchedulerId());
            return new ResponseEntity<Object>(entity.toString(), HttpStatus.valueOf(201));
        }

    @GetMapping(value="/trigger")
    public String trigger(@RequestParam int schedulerId) throws ParseException, UnirestException, IOException, JSONException {
        createScheduledOrder(schedulerId);
        return "Scheduled Order Created Successfully";
    }

    public String createScheduledOrder(int schedulerId) throws IOException, JSONException, ParseException, UnirestException {
        OrderDetails order;
        Gson gson = new Gson();
        List <Scheduler> schedulerlist = getSchedulerDetailsById(schedulerId);
        if (schedulerlist.size() ==0) {
            logger.error("Unable To Find Order Details For Given Scheduler Id");
            return "Unable To Find Order Details For Given Scheduler Id";
        }
        order = gson.fromJson(schedulerlist.get(0).getRequestBody(), OrderDetails.class);
        order.setSchedulerId(schedulerId);
        order.setSchedulerId(schedulerId);
        ResponseEntity<?> s = createOrder(order);
        logger.info("OrderId: "+order.getOrderId());
        order.setIsScheduled("True");
        order.setSchedulerId(schedulerlist.get(0).getSchedulerId());
        order.setSchedulerName(schedulerlist.get(0).getJobname());
        updateOrderDetailsRepository.updateOrder(order.getIsScheduled(),order.getSchedulerId(),order.getOrderId(),order.getSchedulerName());
        logger.info("Successfully Create Order");
        return "Successfully Create Order";
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value="/getSchedulerDetailsById")
    public List<Scheduler> getSchedulerDetailsById(@RequestParam int schedulerId) throws ParseException {
            return schedulerDetailsRepository.findAllBySchedulerId(schedulerId);
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping(value="/deleteScheduler")
    public ResponseEntity<?> deleteSchedulerById(@RequestParam int schedulerId) throws Exception {
        JSONObject entity;
        JobDetail orderJobDetail;
        List<Scheduler> scheduler = schedulerDetailsRepository.findAllBySchedulerId(schedulerId);
        if (scheduler.size() == 0) {
            logger.error("Unable To Find Any Job For Given SchedulerId");
            return new ResponseEntity<Object>(HttpStatus.valueOf(404));
        }
        if (!isNullOrEmpty(scheduler.get(0).getJobId()) && !isNullOrEmpty(scheduler.get(0).getJobname())) {
            try {
                orderJobDetail = quartzScheduler.getJobDetail(new JobKey(scheduler.get(0).getJobId()
                        ,scheduler.get(0).getJobname()));
                if (orderJobDetail != null && orderJobDetail.getKey() != null) {
                    quartzScheduler.deleteJob(orderJobDetail.getKey());
                    updateDeleteStatusInOrderDetails.updateDeleteInOrderDetails("false", schedulerId);
                    schedulerUpdateDetailsRepository.updateScheduler(schedulerId, "Deleted");
                    updateNextrun.updateNextRun(schedulerId, "N/A");
                    logger.info("Job {} deleted successfully", scheduler.get(0).getJobId());
                    entity = new JSONObject();
                    entity.put("scheduler_id", schedulerId);
                    return new ResponseEntity<Object>(entity.toString(), HttpStatus.valueOf(200));
                }
            } catch (SchedulerException ex) {
                logger.error("Failed to delete scheduled job: +", ex);
            }
        }
        logger.error("Failed to delete the selected scheduled job: {}",scheduler.get(0).getJobId());
        return new ResponseEntity<Object>(HttpStatus.valueOf(500));
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value="/pauseScheduledOrder")
    public ResponseEntity<?> inactiveScheduler(@RequestParam int schedulerId) throws Exception {
        JSONObject entity;
        JobDetail orderJobDetail;
        List<Scheduler> scheduler = schedulerDetailsRepository.findAllBySchedulerId(schedulerId);
        if (scheduler.size() == 0) {
            logger.info("Unable To Find Any Job For Given SchedulerId");
            return new ResponseEntity<Object>(HttpStatus.valueOf(404));
        }
        if (!isNullOrEmpty(scheduler.get(0).getJobId()) && !isNullOrEmpty(scheduler.get(0).getJobname())) {
            try {
                orderJobDetail = quartzScheduler.getJobDetail(new JobKey(scheduler.get(0).getJobId(),
                        scheduler.get(0).getJobname()));
                if (orderJobDetail != null && orderJobDetail.getKey() != null) {
                    quartzScheduler.pauseJob(orderJobDetail.getKey());
                    schedulerUpdateDetailsRepository.updateScheduler(schedulerId, "Inactive");
                    logger.info("Job {} paused Successfully", scheduler.get(0).getJobId());
                    entity = new JSONObject();
                    entity.put("scheduler_id", schedulerId);
                    return new ResponseEntity<Object>(entity.toString(), HttpStatus.valueOf(200));
                }
            } catch (SchedulerException ex) {
                logger.error("Failed to pause the job : " + ex);
            }
        }
        logger.error("Failed to pause the selected scheduled job {}",scheduler.get(0).getJobId());
        return new ResponseEntity<Object>("Pausing scheduled job failed", HttpStatus.valueOf(500));

    }

    @CrossOrigin(origins = "*")
    @PostMapping(value="/resumeScheduledOrder")
    public ResponseEntity<?> activateScheduler(@RequestParam int schedulerId) throws Exception {
        JSONObject entity;
        JobDetail orderJobDetail;
        List<Scheduler> scheduler = schedulerDetailsRepository.findAllBySchedulerId(schedulerId);
        if (scheduler.size() == 0) {
            logger.info("Unable To Find Any Job For Given SchedulerId");
            return new ResponseEntity<Object>(HttpStatus.valueOf(404));
        }
        if (!isNullOrEmpty(scheduler.get(0).getJobId()) && !isNullOrEmpty(scheduler.get(0).getJobname())) {
            try {
                orderJobDetail = quartzScheduler.getJobDetail(new JobKey(scheduler.get(0).getJobId(),
                        scheduler.get(0).getJobname()));
                if (orderJobDetail != null && orderJobDetail.getKey() != null) {
                    quartzScheduler.resumeJob(orderJobDetail.getKey());
                    schedulerUpdateDetailsRepository.updateScheduler(schedulerId, "Active");
                    logger.info("Job {} Resumed Successfully", scheduler.get(0).getJobId());
                    entity = new JSONObject();
                    entity.put("scheduler_id", schedulerId);
                    return new ResponseEntity<Object>(entity.toString(), HttpStatus.valueOf(200));
                }
            } catch (SchedulerException ex) {
                logger.error("Failed to resume scheduled job: " + ex);
            }
        }
        logger.error("Failed to resume the selected scheduled job {}",scheduler.get(0).getJobId());
        return new ResponseEntity<Object>("Resuming scheduled job failed", HttpStatus.valueOf(500));
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value="/getAllSchedulerDetails")
    public List<Scheduler> getAllSchedulerDetails() throws ParseException {
        return schedulerDetailsRepository.findAll();
    }


    @CrossOrigin(origins = "*")
    @GetMapping(value="/getOrderBySchedulerId")
    public List<OrderDetails> getOrderBySchedulerId(@RequestParam Integer schedulerId) throws ParseException, UnirestException, JSONException {
        List<OrderDetails> scheduledOrder = orderRepository.findBySchedulerId(schedulerId);
            return scheduledOrder;
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value="/getSuiteDetailsBySchedulerId")
    public String getSuiteDetailsBySchedulerId(@RequestParam Integer schedulerId) throws  JSONException {
        List<OrderDetails> scheduledOrder = orderRepository.findBySchedulerId(schedulerId);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (scheduledOrder.size()==0) {
            logger.info("Unable To Find Any Suite Related Information For Given SchedulerId: "+schedulerId);
            return "Unable To Find Any Suite Related Information For Given SchedulerId: "+schedulerId;
        }
        else {
            JSONObject suiteObject = new JSONObject();
            JSONObject botObject = new JSONObject();
            JSONObject nextRunObject = new JSONObject();
            JSONArray botArray = new JSONArray();
            JSONArray scheduleArray = new JSONArray();
            System.out.println(scheduledOrder.get(0).getSuiteDetails().size());
            for (int i =0;i<scheduledOrder.get(0).getSuiteDetails().size();i++) {
                suiteObject.put("SuiteName",scheduledOrder.get(0).getSuiteDetails().get(i).getSuiteName());
                for (int j =0;j<scheduledOrder.get(0).getSuiteDetails().get(i).getBotDetails().size();j++) {
                    botObject.put("BotName",scheduledOrder.get(0).getSuiteDetails().get(i).getBotDetails().get(0).getBotName());
                    botArray.put(botObject);
                    botObject = new JSONObject();
                }
                suiteObject.put("Bots",botArray);
                botArray = new JSONArray();
                scheduleArray.put(suiteObject);
                suiteObject = new JSONObject();
            }
            List<Scheduler> scheduler = schedulerDetailsRepository.findAllBySchedulerId(schedulerId);
            Date nextRun = null;
            TriggerKey triggerKey;
            triggerKey = new TriggerKey(scheduler.get(0).getJobId(), scheduler.get(0).getJobname());
            try {
                nextRun = quartzScheduler.getTrigger(triggerKey).getNextFireTime();
                logger.debug("Get next run time : {}",dateFormat.format(nextRun));
            } catch (SchedulerException e) {
                logger.error("Failed to fetch next run" + e);
            }
            if (!isNullOrEmpty(String.valueOf(nextRun)) && !scheduler.get(0).getJobStatus().equalsIgnoreCase("Inactive")) {
                nextRunObject.put("next_run_time", dateFormat.format(nextRun));
            } else {
                nextRunObject.put("next_run_time", "Inactive");
            }
            scheduleArray.put(nextRunObject);
            return scheduleArray.toString();
        }
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/updateSchedulerById")
    public ResponseEntity<?> updateSchedulerById(@RequestBody Scheduler scheduler) throws IOException, JSONException {

        if (isNullOrEmpty(scheduler.getMinute()) || isNullOrEmpty(scheduler.getMonth()) || isNullOrEmpty(scheduler.getDay_of_week()) || isNullOrEmpty(scheduler.getDay()) || isNullOrEmpty(scheduler.getHour()) == true) {
            logger.error("Bad Scheduler Input For Update");
            return new ResponseEntity<Object>("Invalid Scheduler input", HttpStatus.valueOf(400));
        }
        List<Scheduler> schedulerList = schedulerDetailsRepository.findAllBySchedulerId(scheduler.getSchedulerId());
        if (schedulerList.size() == 0) {
            logger.error("Unable To Found Any Schedule Information For Given ScheduleID: {} ", scheduler.getSchedulerId());
            return new ResponseEntity<Object>("Unable To Found Any Schedule Information For Given ScheduleID: " + scheduler.getSchedulerId(), HttpStatus.valueOf(404));
        }
        String cronValue;
        JSONObject entity;
        if (scheduler.getInput().size() == 0) {
            //Update is for schedule time
            cronValue = rafService.buildCronValue(scheduler);
            if (!CronExpression.isValidExpression(cronValue)) {
                logger.error("Invalid scheduling time inputs");
                return new ResponseEntity<Object>("Invalid schedule input", HttpStatus.valueOf(400));
            }
            logger.debug("cron expression: {}", cronValue);
            JobDetail getOrderJobDetail = null;
            if (!isNullOrEmpty(schedulerList.get(0).getJobId()) && !isNullOrEmpty(schedulerList.get(0).getJobname())) {
                try {
                    JobKey orderJobKey = new JobKey(schedulerList.get(0).getJobId(), schedulerList.get(0).getJobname());
                    getOrderJobDetail = quartzScheduler.getJobDetail(orderJobKey);
                    if (getOrderJobDetail != null) {
                        logger.debug("JobKey : " + getOrderJobDetail.getKey());
                        Trigger trigger = rafService.buildJobTrigger(getOrderJobDetail, cronValue.toString(), schedulerList.get(0));
                        logger.debug("TriggerId: " + trigger.getKey());
                        
                        
                        quartzScheduler.rescheduleJob(trigger.getKey(), trigger);
                        updateScheduleDetails.updateSchedule(scheduler.getSchedulerId(), scheduler.getMonth(), scheduler.getDay_of_week(), scheduler.getDay(), scheduler.getMinute(), scheduler.getHour(), schedulerList.get(0).getRequestBody());
                        entity = new JSONObject();
                        entity.put("scheduler_id", scheduler.getSchedulerId());
                        logger.info("Scheduled Job update successful for Id: {}",scheduler.getSchedulerId());
                        return new ResponseEntity<Object>(entity.toString(), HttpStatus.valueOf(201));
                    } else {
                        logger.error("No scheduled job found for id {}", schedulerList.get(0).getJobId());
                    }
                } catch (SchedulerException e) {
                    logger.error("updateJob failed " + e);
                }
            } else {
                logger.error("JobId and Jobname should not be null or empty");
            }
            return new ResponseEntity<Object>("Unable to update scheduled job details", HttpStatus.valueOf(500));
        } else {
            //Update testsuite execution
            scheduler.setRequestBody(scheduler.getInput().toJSONString());
            updateScheduleDetails.updateSchedule(scheduler.getSchedulerId(), scheduler.getMonth(), scheduler.getDay_of_week(), scheduler.getDay(), scheduler.getMinute(), scheduler.getHour(), scheduler.getRequestBody());
            entity = new JSONObject();
            entity.put("scheduler_id", scheduler.getSchedulerId());
            logger.info("Scheduled Job update successful for Id: {}", scheduler.getSchedulerId());
            return new ResponseEntity<Object>(entity.toString(), HttpStatus.valueOf(201));
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value="/getScheduleList")
    public List<Scheduler> getScheduleList(@RequestParam List<String> role) throws  JSONException {
        Date nextRun = null;
        TriggerKey triggerKey;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<Scheduler> scheduleList = schedulerDetailsRepository.findAll();
        for (int i = 0; i < scheduleList.size(); i++) {
            int schedulerId = scheduleList.get(i).getSchedulerId();
            List<Scheduler> scheduler = schedulerDetailsRepository.findAllBySchedulerId(schedulerId);
            logger.debug(scheduleList.get(i).getJobId(), scheduleList.get(i).getJobname());
            if (!isNullOrEmpty(scheduleList.get(i).getJobId()) && !isNullOrEmpty(scheduleList.get(i).getJobname())) {
                triggerKey = new TriggerKey(scheduleList.get(i).getJobId(), scheduleList.get(i).getJobname());
                try {
                    if (quartzScheduler.getTrigger(triggerKey) != null) {
                        nextRun = quartzScheduler.getTrigger(triggerKey).getNextFireTime();
                        logger.debug("Get next run time : {}", dateFormat.format(nextRun));
                    }
                } catch (SchedulerException e) {
                    logger.error("Failed to fetch next run" + e);
                }
                if (nextRun != null && !isNullOrEmpty(String.valueOf(nextRun)) && !scheduleList.get(i).getJobStatus().equalsIgnoreCase("Inactive")) {
                    updateNextrun.updateNextRun(schedulerId, dateFormat.format(nextRun));
                } else {
                    if (scheduleList.get(i).getNextRun() != null && !scheduleList.get(i).getNextRun().equals("N/A")) {
                        updateNextrun.updateNextRun(schedulerId, "Inactive");
                    }
                }
            }
        }
        List<Scheduler> schedulerDetails=schedulerDetailsRepository.findByRoleIn(role);
        Collections.reverse(schedulerDetails);
        return schedulerDetails;
    }
}