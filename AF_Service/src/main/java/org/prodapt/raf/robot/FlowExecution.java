package org.prodapt.raf.robot;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

public class FlowExecution extends Thread{

    private String noderedurl;
    private JSONObject requestbody;
    private String flowname;

    public FlowExecution(JSONObject requestbody,String noderedurl) {
        this.requestbody = requestbody;
        this.noderedurl = noderedurl;
    }

    public void run(){

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try {
            HttpPost request = new HttpPost(noderedurl);
            StringEntity params = new StringEntity(requestbody.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            httpClient.execute(request);
        }catch (Exception ex) {
            System.out.println("Failed to reach NODE-RED or uri is Invalid");
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                System.out.println("Failed to reach NODE-RED or uri is Invalid");
            }
        }
    }


}

