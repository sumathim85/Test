package org.prodapt.raf.model;

import org.hibernate.annotations.CreationTimestamp;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="Scheduler")
public class Scheduler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer schedulerId;

    @Transient
    private JSONObject input;

    @Lob
    private String requestBody;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date jobCreatedAt;

    private String jobStatus;

    private String jobname;

    private String month;

    private String day_of_week;

    private String day;

    private String hour;

    private String minute;

    private String jobId;

    private String confType;

    private String serviceType;

    private String orderType;

    private String nextRun;

    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNextRun() {
        return nextRun;
    }

    public void setNextRun(String nextRun) {
        this.nextRun = nextRun;
    }

    public String getConfType() {
        return confType;
    }

    public void setConfType(String confType) {
        this.confType = confType;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getSchedulerId() {
        return schedulerId;
    }

    public void setSchedulerId(Integer schedulerId) {
        this.schedulerId = schedulerId;
    }

    public JSONObject getInput() {
        return input;
    }

    public void setInput(JSONObject input) {
        this.input = input;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public Date getJobCreatedAt() {
        return jobCreatedAt;
    }

    public void setJobCreatedAt(Date jobCreatedAt) {
        this.jobCreatedAt = jobCreatedAt;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getJobname() {
        return jobname;
    }

    public void setJobname(String jobname) {
        this.jobname = jobname;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay_of_week() {
        return day_of_week;
    }

    public void setDay_of_week(String day_of_week) {
        this.day_of_week = day_of_week;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }
}
