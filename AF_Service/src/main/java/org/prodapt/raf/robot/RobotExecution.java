package org.prodapt.raf.robot;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.json.JSONObject;
import org.prodapt.raf.controller.MailerController;
import org.prodapt.raf.model.OrderDetails;
import org.prodapt.raf.model.SubOrderDetails;
import org.prodapt.raf.service.RafService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class RobotExecution implements Runnable{


    private RafService rafService;
    private SubOrderDetails subOrderDetails;
    private OrderDetails orderDetails;
    private String pathForAccess;
    private int orderid;
    private String folderPath;
    private boolean rerun;
    private Integer suborderid;
    private  String elasticIP;
    private Integer elasticPort;
    private String dbUrl;
    private static Logger log = LoggerFactory.getLogger(RobotExecution.class);



    public RobotExecution(OrderDetails orderDetails, String pathForAccess, int orderid, String folderPath, boolean rerun, Integer suborderid, RafService rafService, String elasticIP, Integer elasticPort,String dbUrl) {
        this.orderDetails = orderDetails;
        this.pathForAccess = pathForAccess;
        this.orderid = orderid;
        this.folderPath = folderPath;
        this.rerun = rerun;
        this.suborderid = suborderid;
        this.rafService = rafService;
        this.elasticPort=elasticPort;
        this.elasticIP=elasticIP;
        this.dbUrl=dbUrl;
    }

    public RobotExecution(SubOrderDetails subOrderDetails, String pathForAccess, int orderid, String folderPath, boolean rerun, Integer suborderid,RafService rafService,String elasticIP,Integer elasticPort,String dbUrl) {
        this.subOrderDetails = subOrderDetails;
        this.pathForAccess = pathForAccess;
        this.orderid = orderid;
        this.folderPath = folderPath;
        this.rerun = rerun;
        this.suborderid = suborderid;
        this.rafService =rafService;
        this.elasticIP=elasticIP;
        this.elasticPort=elasticPort;
        this.dbUrl=dbUrl;
    }

    /**
     * Executor.
     *For separate orders separate threads are created and this function is invoked.
     * @throws IOException the io exception
     * Robot code is executed based on the arguments passed from the UI.
     * Python is invoked to parse the xml and update the DB based on the xml generated from the robot execution and the thread moves to inactive state
     */
    public void executor() throws IOException {
        log.info(elasticIP);
        log.info(folderPath);
        String confType = new String();
        Integer Id;
        String robotFilePath = new String();
        String testPlanPath = new String();
        String rollback = "no";
        Integer counter =0;
        RestHighLevelClient client = null;
        if(orderDetails.getSchedulerId()!=0) {
            client = new RestHighLevelClient(RestClient.builder(new HttpHost(elasticIP, elasticPort, "http")));
            GetIndexRequest request = new GetIndexRequest("scheduler");
            if(client.indices().exists(request, RequestOptions.DEFAULT)==false){
                log.error("index doesn't exist in "+elasticIP);
            }
        }


        if (rerun){
            confType = subOrderDetails.getConfType().toUpperCase();
            Id = subOrderDetails.getOrderId();
        }
        else{
            confType = orderDetails.getConfType().toUpperCase();
            Id = orderDetails.getOrderId();
        }
        robotFilePath = "Trunk/RAF_Automation/src/robot/testsuites/"+confType;

        log.info(robotFilePath);
        testPlanPath = folderPath + "/testplan_" + Id +".txt";
        rafService.logToDashboard("Order Test plan path"+testPlanPath,"INFO",orderid);
        Process p=null;
        System.out.println(dbUrl);
        ProcessBuilder robotProcess = new ProcessBuilder("robot","-v Id:" +orderid,"-v db_url:" +dbUrl,"-v suborder_id:" +suborderid,"-v Dir:" +folderPath  , "--argumentfile" , testPlanPath , "--outputdir" , folderPath , robotFilePath);
        rafService.logToDashboard("Order Execution is Stared By Bots","INFO",orderid);


        String robotExecDir=new String();

//        if(confType.equals("RESTCONF")) {
//            robotExecDir = pathForAccess + "VRobot/";
//            rafService.logToDashboard("Order Execution path"+robotExecDir,"INFO",orderid);
//        }
//        else {
//
//        }
        robotExecDir = pathForAccess + "RobotFiles/";
        rafService.logToDashboard("Order Execution path"+robotExecDir,"INFO",orderid);
        robotProcess.directory(new File(robotExecDir));


        p= robotProcess.start();

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));


        String streamReader;
        String output = new String();
        while ((streamReader = stdInput.readLine()) != null) {
            output=output+streamReader+'\n';

        }
        String out=output;

        if(output.length() != 0) {
            rafService.logToDashboard("Order Execution is done by bots", "INFO", orderid);
            log.info("Robot: " + output);
            rafService.logToDashboard("output : \n" + output, "INFO", orderid);
            rafService.logToDashboard("Order Execution is Successfully Completed", "INFO", orderid);
            if(orderDetails.getSchedulerId()!=0) {
                rafService.elkconnect(output, orderDetails, client);
            }
        }


        try {
            FileWriter myWriter = new FileWriter(folderPath+"/robotoutput.txt");
            myWriter.write(output);
            myWriter.close();
            log.info("Successfully wrote to the file.");
        } catch (IOException e) {
            log.error("robot logs failed for ID "+Id);
        }

        if(out.length() == 0) {
            output=new String();
            while ((streamReader = stdError.readLine()) != null) {
                output=output+streamReader+'\n';
            }
            log.error("Robot Error:" + output + '\n');
            rafService.logToDashboard("Order Execution is Failed : \n"+output,"FAIL",orderid);
        }

    }

    @Override
    public void run() {
        try {
            this.executor();
        } catch (IOException e) {
            log.error("Failed To Execute");
            rafService.logToDashboard("Order Execution is Failed : \n"+e.toString(),"FAIL",orderid);
            e.printStackTrace();
        }
    }

    public SubOrderDetails getSubOrderDetails() {
        return subOrderDetails;
    }

    public void setSubOrderDetails(SubOrderDetails subOrderDetails) {
        this.subOrderDetails = subOrderDetails;
    }
}


