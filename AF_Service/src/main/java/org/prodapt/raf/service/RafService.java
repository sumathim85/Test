package org.prodapt.raf.service;

import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.prodapt.raf.exception.CreationFailException;
import org.prodapt.raf.exception.DatabaseException;
import org.prodapt.raf.model.*;
import org.prodapt.raf.repository.*;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.prodapt.raf.dto.Mail;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Service("rafService")
public class RafService {

    @Autowired
    SchdulerRepository schdulerRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    SubOrderRepository subOrderRepository;
    @Autowired
    SuiteRepository suiteRepository;
    @Autowired
    MailerService emailService;

    public void logToDashboard(String msg , String log,int id){
        Logger logger = LoggerFactory.getLogger(RafService.class);
        System.out.println(id+"+++++++++++++++++++++++++++++++++++"+msg);
        String role="";
        if(log =="FAIL")
        {
            try {
//                Mail mail = new Mail();
//                mail.setFrom("testportaluser@prodapt.com");// replace with your desired email
//                mail.setMailTo("sathishkumar.sv@prodapt.com".split(","));
//                mail.setMailCc("beerappa.m@prodapt.com , padmanaban.n@prodapt.com".split(","));
//                mail.setMailBcc("".split(","));
//                mail.setSubject("Critical Order Failure For Order id " + id);
//                Map<String, Object> model = new HashMap<String, Object>();
//                model.put("orderId", id);
//                model.put("msg", msg);
//                model.put("actualTo", "sathishkumar.sv@prodapt.com");
//                model.put("actualCc", "beerappa.m@prodapt.com , padmanaban.n@prodapt.com");
//                model.put("actualBcc", "");
//                model.put("Status","FAIL");
//                model.put("entity", "RafFailure");
//                model.put("bg", true);
//                mail.setProps(model);
//                emailService.sendEmail(mail);
                List<OrderDetails> order=orderRepository.findByOrderId(id);
                orderRepository.save(order.get(0));

            }
            catch (Exception e)
            {
                logToDashboard("Error in mail sending Order id "+id,"ERROR",id);
                e.printStackTrace();
            }
        }
        else
        {
            List<OrderDetails> order=orderRepository.findByOrderId(id);
            System.out.println(order);
            role=order.get(0).getRole();
            System.out.println(order.get(0).getConfType());
        }
        String flag="demo";
        MDC.put("order_id", Integer.toString(id));
        MDC.put("Extra", flag);
        MDC.put("log_level", log);
        MDC.put("role", role);
        MDC.put("keyword", "'BuiltIn.Log'");
        logger.info(msg);
        MDC.remove("role");
        MDC.remove("keyword");
        MDC.remove("Extra");
        MDC.remove("order_id");
        MDC.remove("log_level");

    }

    public void folderCreation(OrderDetails orderDetails, String folderPath){
        logToDashboard("Test Execution Folder creation is stared for Order id "+orderDetails.getOrderId(),"INFO",orderDetails.getOrderId());
        File file = new File(folderPath);
        boolean folder = file.mkdirs();
        if (!folder)
        {    logToDashboard("Error in creating a Execution folder Creation for order id for Order id "+orderDetails.getOrderId(),"FAIL",orderDetails.getOrderId());
            System.out.println("Error in creating a folder for order id:"+orderDetails.getOrderId());
            throw new CreationFailException("folder for orderId:"+orderDetails.getOrderId());
        }
        logToDashboard("Creating a Execution folder Creation for order id for Order id "+orderDetails.getOrderId()+" Is Successfully Competed","INFO",orderDetails.getOrderId());
    }

    public void folderCreationForSuborder(SubOrderDetails subOrderDetails, String folderPath, Integer suborderid){

        File file = new File(folderPath);
        boolean folder = file.mkdirs();
        logToDashboard("Test Execution folder Creation is started for Suborder id "+suborderid,"INFO",suborderid);
        if (!folder)
        {   logToDashboard("Error in Creating Test Execution folder for Suborder id "+suborderid,"FAIL",suborderid);
            System.out.println("Error in creating a folder for Sub order id: "+suborderid);
            throw new CreationFailException("folder for Sub orderId: "+suborderid);
        }
        logToDashboard("Test Execution folder Creation is successfully completed for Suborder id "+suborderid,"INFO",suborderid);
    }


    public Integer addOrderDetails(OrderDetails orderDetails){

        try {
            orderDetails.setOrderStatus("Accepted");
            orderDetails.setTeam(orderDetails.getTeam());
            OrderDetails orderDetail = orderRepository.save(orderDetails);
            logToDashboard("Order Processing Started by RAF Engine "+orderDetails.getOrderId(),"INFO",+orderDetails.getOrderId());
            logToDashboard("Adding Order Details to the Data Base for the Order Id "+orderDetails.getOrderId()+" Successfully Completed","INFO",+orderDetails.getOrderId());
            return orderDetail.getOrderId().intValue();
        }
        catch (Exception e) {
            logToDashboard("Adding Order Details to the Data Base Failed "+e.toString(),"FAIL",0);
            throw new DatabaseException(e.toString());
        }


    }

    public void addSubOrderDetails(SubOrderDetails subOrderDetails, Integer suborderid){
        try {
            subOrderDetails.setOrderStatus("Accepted");
            subOrderDetails.setSubOrderId(suborderid);
            logToDashboard("Adding Order Details to the Data Base for the SubOrder Id "+suborderid+"Successfully Completed","INFO",suborderid);
            SubOrderDetails subOrderDetail = subOrderRepository.save(subOrderDetails);
        }
        catch (Exception e)
        {
            logToDashboard("Adding Order Details to the Data Base Failed for the SubOrder Id "+e.toString(),"FAIL",0);
            throw new DatabaseException(e.toString());
        }
    }

    public void updateSuiteDetails(OrderDetails orderDetails,Integer orderid,Integer suborderid){
        try {
            for (int i = 0; i < orderDetails.getSuiteDetails().size(); i++) {
                orderDetails.getSuiteDetails().get(i).setOrderId(orderid);
                orderDetails.getSuiteDetails().get(i).setSubOrderId(suborderid);
                OrderDetails orderDetail = orderRepository.save(orderDetails);
            }
            logToDashboard("Updating suite Details to the Data Base Successfully Completed for the SubOrder Id "+suborderid+" Order Id "+orderid,"INFO",orderid);
        }
        catch (Exception e)
        {
            logToDashboard("Updating suite Details to the Data Base Failed for the Order Id "+suborderid+" Order Id "+orderid+":"+e.toString(),"FAIL",orderid);
            throw new DatabaseException(e.toString()+suborderid);
        }
    }

    public void updateSuiteDetailsForSuborder(SubOrderDetails subOrderDetails,Integer orderid,Integer suborderid){
        try {
            for (int i = 0; i < subOrderDetails.getSuiteDetails().size(); i++) {
                subOrderDetails.getSuiteDetails().get(i).setOrderId(orderid);
                subOrderDetails.getSuiteDetails().get(i).setSubOrderId(suborderid);
                SubOrderDetails subOrderDetail = subOrderRepository.save(subOrderDetails);
            }
            logToDashboard("Updating suite Details to the Data Base Successfully Completed for the SubOrder Id "+suborderid+" Order Id "+orderid,"INFO",orderid);

        }
        catch (Exception e)
        {
            logToDashboard("Updating suite Details to the Data Base Failed for the Sub Order Id "+suborderid+" Order Id "+orderid+":"+e.toString(),"FAIL",orderid);
            throw new DatabaseException(e.toString()+suborderid+"Order Id"+orderid);
        }
    }

    public void updateBotDetails(OrderDetails orderDetails,Integer orderid,Integer suborderid){
        try {
            for (int i = 0; i < orderDetails.getSuiteDetails().size(); i++) {
                for (int j = 0; j < orderDetails.getSuiteDetails().get(i).getBotDetails().size(); j++) {
                    orderDetails.getSuiteDetails().get(i).getBotDetails().get(j).setOrderId(orderid);
                    orderDetails.getSuiteDetails().get(i).getBotDetails().get(j).setSuiteId(orderDetails.getSuiteDetails().get(i).getSuiteId());
                    orderDetails.getSuiteDetails().get(i).getBotDetails().get(j).setSuiteName(orderDetails.getSuiteDetails().get(i).getSuiteName());
                    orderDetails.getSuiteDetails().get(i).getBotDetails().get(j).setSubOrderId(suborderid);
                    OrderDetails orderDetail = orderRepository.save(orderDetails);
                }
            }
            logToDashboard("Updating Bot Details to the Data Base Successfully Completed for the SubOrder Id "+suborderid+" Order Id "+orderid,"INFO",orderid);

        }
        catch (Exception e)
        {
            logToDashboard("Updating Suite Details to the Data Base Failed for the Sub Order Id "+suborderid+" Order Id "+orderid+":"+e.toString(),"FAIL",orderid);
            throw new DatabaseException(e.toString()+suborderid+"Order Id"+orderid);
        }
    }

    public void updateBotDetailsForSuborder(SubOrderDetails subOrderDetails,Integer orderid,Integer suborderid){
        try {
            for (int i = 0; i < subOrderDetails.getSuiteDetails().size(); i++) {
                for (int j = 0; j < subOrderDetails.getSuiteDetails().get(i).getBotDetails().size(); j++) {
                    if (subOrderDetails.getSuiteDetails().get(i).getBotDetails().get(j).isRollback().equals("yes")) {
                        String botname = subOrderDetails.getSuiteDetails().get(i).getBotDetails().get(j).getBotName() + " RollBack";
                        subOrderDetails.getSuiteDetails().get(i).getBotDetails().get(j).setBotName(botname);
                    }
                    subOrderDetails.getSuiteDetails().get(i).getBotDetails().get(j).setOrderId(orderid);
                    subOrderDetails.getSuiteDetails().get(i).getBotDetails().get(j).setSuiteId(subOrderDetails.getSuiteDetails().get(i).getSuiteId());
                    subOrderDetails.getSuiteDetails().get(i).getBotDetails().get(j).setSubOrderId(suborderid);
                    subOrderDetails.getSuiteDetails().get(i).getBotDetails().get(j).setSuiteName(subOrderDetails.getSuiteDetails().get(i).getSuiteName());
                    subOrderDetails.getSuiteDetails().get(i).getBotDetails().get(j).setRollback(subOrderDetails.getSuiteDetails().get(i).getBotDetails().get(j).isRollback());
                    SubOrderDetails subOrderDetail = subOrderRepository.save(subOrderDetails);
                }
            }
            logToDashboard("Updating Bot Details to the Data Base Successfully Completed for the SubOrder Id "+suborderid+" Order Id "+orderid,"INFO",orderid);

        }
        catch (Exception e)
        {
            logToDashboard("Updating Bot Details to the Data Base Failed for the Sub Order Id "+suborderid+" Order Id "+orderid+":"+e.toString(),"FAIL",orderid);
            throw new DatabaseException(e.toString()+suborderid+"Order Id"+orderid);
        }
    }

    public void testPlanCreation(OrderDetails orderDetails, String folderPath) throws IOException, JSONException {

        Set<String> set = new LinkedHashSet<>();
        String testCases=new String();
        JSONObject jsonObj = new JSONObject();
        try{
            logToDashboard("Test Plan Creation for stared for the Order Id "+orderDetails.getOrderId(),"INFO",orderDetails.getOrderId());

            for (int i = 0; i < orderDetails.getSuiteDetails().size(); i++) {
                for (int j=0 ;j< orderDetails.getSuiteDetails().get(i).getBotDetails().size();j++){

                    JSONObject deviceJsonObject = new JSONObject(orderDetails.getSuiteDetails().get(i).getBotDetails().get(j).getInput().toString());
                    jsonObj.put("input",deviceJsonObject);

                    if (deviceJsonObject.has("Orchestrator"))
                        set.add(deviceJsonObject.getString("Orchestrator"));

                    testCases = testCases + System.lineSeparator() + "-t " + orderDetails.getSuiteDetails().get(i).getBotDetails().get(j).getBotName().toString();

                    testCases = testCases + System.lineSeparator() + "-v " + orderDetails.getSuiteDetails().get(i).getBotDetails().get(j).getBotName().toString().toLowerCase().replace(" ",
                            "_") + ":" + jsonObj.toString().replaceAll("\\\\","");

                }
            }
            if (!set.isEmpty()) {
                List<String> argument = new ArrayList<>();
                argument.addAll(set);
                testCases = testCases + System.lineSeparator() + "-v Orchestrator:" + argument.get(0);
            }
            try {
                logToDashboard("Test Plan :"+testCases,"INFO",orderDetails.getOrderId());
                System.out.println(testCases);
                FileWriter myWriter = new FileWriter(folderPath+"/testplan_"+orderDetails.getOrderId()+".txt");
                myWriter.write(testCases);
                myWriter.close();
                logToDashboard("Test Plan Creation for successfully completed for the Order Id "+orderDetails.getOrderId(),"INFO",orderDetails.getOrderId());
                System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                logToDashboard("Test Plan Creation for Failed for the Order Id "+orderDetails.getOrderId()+":"+e.toString(),"FAIL",orderDetails.getOrderId());
                System.out.println("Test plan creation failed for orderID inside suborder id"+orderDetails.getOrderId()+"/"+orderDetails.getSubOrderId());
                throw new CreationFailException("testplan for orderId inside suborder id:"+orderDetails.getOrderId()+"/"+orderDetails.getSubOrderId());
            }
        }
        catch (Exception e) {
            logToDashboard("Test Plan Creation for FAILED for the Order Id "+orderDetails.getOrderId()+": "+e.toString(),"FAIL",orderDetails.getOrderId());
            System.out.print("Test Plan Creation Failed...");
        }

    }

    public void testPlanCreationForSuborder(SubOrderDetails subOrderDetails, String folderPath, Integer counter) throws IOException, JSONException {
        Set<String> set = new LinkedHashSet<>();
        String testCases=new String();
        JSONObject jsonObj = new JSONObject();
        try{
            for (int i = 0; i < subOrderDetails.getSuiteDetails().size(); i++) {
                for (int j=0 ;j< subOrderDetails.getSuiteDetails().get(i).getBotDetails().size();j++){

                    JSONObject deviceJsonObject = new JSONObject(subOrderDetails.getSuiteDetails().get(i).getBotDetails().get(j).getInput().toString());
                    jsonObj.put("input",deviceJsonObject);

                    if (deviceJsonObject.has("Orchestrator"))
                        set.add(deviceJsonObject.getString("Orchestrator"));

                    if(subOrderDetails.getSuiteDetails().get(i).getBotDetails().get(j).isRollback().equals("no")){

                        testCases = testCases + System.lineSeparator() + "-t " + subOrderDetails.getSuiteDetails().get(i).getBotDetails().get(j).getBotName().toString();

                        testCases = testCases + System.lineSeparator() + "-v " + subOrderDetails.getSuiteDetails().get(i).getBotDetails().get(j).getBotName().toString().toLowerCase().replace(" ",
                                "_") + ":" + jsonObj.toString().replaceAll("\\\\","");
                    }else{

                        String rollbackJson = rollbackTestCaseFormation(jsonObj,subOrderDetails.getSuiteDetails().get(i).getBotDetails().get(j).getBotName(),subOrderDetails.getOrderId());

                        testCases = testCases + System.lineSeparator() + "-t " + subOrderDetails.getSuiteDetails().get(i).getBotDetails().get(j).getBotName().toString();;

                        testCases = testCases + System.lineSeparator() + "-v " + subOrderDetails.getSuiteDetails().get(i).getBotDetails().get(j).getBotName().toLowerCase().replace(" ", "_") + ":" + rollbackJson;

                    }

                }
            }

            if (!set.isEmpty()) {
                List<String> argument = new ArrayList<>();
                argument.addAll(set);
                testCases = testCases + System.lineSeparator() + "-v Orchestrator:" + argument.get(0);
            }

            System.out.println(testCases);
            try {
                FileWriter myWriter = new FileWriter(folderPath+"/testplan_"+subOrderDetails.getOrderId()+".txt");
                myWriter.write(testCases);
                myWriter.close();
                System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                logToDashboard("Test Plan Creation for FAILED for the Order Id "+subOrderDetails.getOrderId()+": "+e.toString(),"FAIL",subOrderDetails.getOrderId());
                System.out.println("Test plan creation failed for orderID inside suborder id "+subOrderDetails.getOrderId()+"/"+subOrderDetails.getSubOrderId());
                throw new CreationFailException("testplan for orderId inside suborder id: "+subOrderDetails.getOrderId()+"/"+subOrderDetails.getSubOrderId());
            }
        }
        catch (Exception e) {
            logToDashboard("Test Plan Creation for FAILED for the Order Id "+subOrderDetails.getOrderId()+": "+e.toString(),"FAIL",subOrderDetails.getOrderId());
            System.out.print("Test Plan Creation Failed...");
        }

    }

     public String GetReport(ByteArrayResource resource, String pathForAccess, List<Integer> id, File file,String Filename) throws IOException {
        String test= null;

        for(int i=0;i< id.size();i++) {
            String folderPath = pathForAccess + "RobotLogs/" + id.get(i) + Filename;



            try {
                file = new File(folderPath);
                Path path = Paths.get(file.getAbsolutePath());
                resource = new ByteArrayResource(Files.readAllBytes(path));

                test = new String(resource.getByteArray(), StandardCharsets.UTF_8);
                if(Filename.equals("/report.html")) {

                    String logPath = "log.html?id=" + id.get(i);
                    System.out.println(logPath);
                    test = test.replace("log.html", logPath);
                    System.out.println(test.replace("log.html", logPath));

                }
                else if (Filename.equals("/log.html")){
                    test = test.replace("<a href=\"${relativeSource}\">", "");
//                        System.out.println(test.replace("log.html", logPath));


                }

            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

        }
        return test;
    }



    public String rollbackTestCaseFormation(JSONObject rollbackJson,String botname,Integer orderid) throws JSONException {
        JSONArray finalDeviceList = new JSONArray();
        JSONObject devObj = new JSONObject();
        JSONObject rollback = new JSONObject();
        JSONObject testcasename = new JSONObject();
        StringBuffer rollbackComment = new StringBuffer();
        JSONObject devobjComment = new JSONObject();
        JSONObject devobjName = new JSONObject();

        JSONObject input = (JSONObject) rollbackJson.get("input");
        JSONArray device = (JSONArray) input.get("device");

        String testname = botname.replace(" ","_");
        rollbackComment.append(testname);
        rollbackComment.append("_");
        rollbackComment.append(orderid);

        devobjName.put("testcasename",rollbackComment);
        devobjComment.put("rollback",devobjName);

        for(int i=0; i<device.length(); i++){
            devObj = (JSONObject)  device.get(i);
            devObj.remove("configuration");
            devObj.put("configuration",devobjComment);
            finalDeviceList.put(devObj);
        }
        rollback.put("device",finalDeviceList);
        testcasename.put("input",rollback);

        return testcasename.toString();
    }

    public void updateFlowDeails(OrderDetails orderDetails,Integer orderid){
        orderDetails.getFlowDetails().get(0).setFlowName(orderDetails.getFlowDetails().get(0).getFlowName());
        orderDetails.getFlowDetails().get(0).setOrderId(orderid);
        orderDetails.getFlowDetails().get(0).setTriggerType(orderDetails.getFlowDetails().get(0).getTriggerType());
    }

    public int addSchdulerDetails(Scheduler scheduler) throws ParseException {
        scheduler.setRequestBody(scheduler.getInput().toJSONString().replaceAll("\\\\",""));

        scheduler.setConfType(scheduler.getInput().get("confType").toString());
        scheduler.setOrderType(scheduler.getInput().get("orderType").toString());
        scheduler.setServiceType(scheduler.getInput().get("serviceType").toString());
        scheduler = schdulerRepository.save(scheduler);
        return scheduler.getSchedulerId().intValue();
    }

    public String buildCronValue(Scheduler scheduler) {
        StringBuilder cronValue = new StringBuilder();
        if(scheduler.getMinute().equals("*")) {
            scheduler.setMinute("0");
        }
        cronValue = cronValue.append("0").append(" ") //seconds
                .append(scheduler.getMinute()).append(" ")//minutes
                .append(scheduler.getHour()).append(" ")//hours
                .append(scheduler.getDay()).append(" ")//day
                .append(scheduler.getMonth()).append(" ")//month
                .append(scheduler.getDay_of_week());//days of week
        return String.valueOf(cronValue);
    }

    public JobDetail buildJobDetail(Scheduler scheduler) {
        JobDataMap jobDataMap = new JobDataMap();

        return JobBuilder.newJob(ScheduledOrderJobService.class)
                .withIdentity(UUID.randomUUID().toString(), scheduler.getJobname())
                .withDescription("Create Order Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    public Trigger buildJobTrigger(JobDetail jobDetail, String cronValue, Scheduler scheduler) {
        return newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), scheduler.getJobname())
                .withDescription("Create order Trigger")
                .withSchedule(cronSchedule(cronValue).withMisfireHandlingInstructionDoNothing())
                .build();
    }

    public void elkconnect(String output, OrderDetails orderDetails, RestHighLevelClient client) throws IOException {
        int count = StringUtils.countMatches(output, "FAIL");
        IndexRequest indexRequest;
        for (int i = 0; i < orderDetails.getSuiteDetails().size(); i++) {
            for (int j = 0; j < orderDetails.getSuiteDetails().get(i).getBotDetails().size(); j++) {
                String[] lines = output.split(orderDetails.getSuiteDetails().get(i).getBotDetails().get(j).getBotName().toString());
                String[] splits = lines[1].split("\n");
                String[] testcaseResult = splits[0].split("\\s+");
                if (count == 0) {
                    indexRequest = new IndexRequest("scheduler")
                            .source(jsonBuilder()
                                    .startObject()
                                    .field("order_Id", orderDetails.getOrderId())
                                    .field("scheduler_Id", orderDetails.getSchedulerId())
                                    .field("Result", "PASS")
                                    .field("PASS", 1)
                                    .field("FAIL", 0)
                                    .field("TestcaseName", orderDetails.getSuiteDetails().get(i).getBotDetails().get(j).getBotName())
                                    .field("TestcaseResult", testcaseResult[2])
                                    .field("updated_time", new Date())
                                    .field("Role",orderDetails.getRole())
                                    .endObject()
                            );
                    client.index(indexRequest, RequestOptions.DEFAULT);

                } else {
                    indexRequest = new IndexRequest("scheduler")
                            .source(jsonBuilder()
                                    .startObject()
                                    .field("order_Id", orderDetails.getOrderId())
                                    .field("scheduler_Id", orderDetails.getSchedulerId())
                                    .field("Result", "FAIL")
                                    .field("FAIL", 1)
                                    .field("PASS", 0)
                                    .field("TestcaseName", orderDetails.getSuiteDetails().get(i).getBotDetails().get(j).getBotName())
                                    .field("TestcaseResult", testcaseResult[2])
                                    .field("updated_time", new Date())
                                    .field("Role",orderDetails.getRole())
                                    .endObject()
                            );
                    client.index(indexRequest, RequestOptions.DEFAULT);

                }


            }
        }

        client.close();

    }




}