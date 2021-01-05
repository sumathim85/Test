package org.prodapt.raf.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import org.json.simple.JSONObject;

@Entity
@Table(name="BotDetails")
public class BotDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer botId;

    private String botName;

    public Integer getSubOrderId() {
        return subOrderId;
    }

    public void setSubOrderId(Integer subOrderId) {
        this.subOrderId = subOrderId;
    }

    private Integer subOrderId;

    private String isCritical;

    @CreationTimestamp
    @Temporal(value = TemporalType.TIME)
    private Date elapsed;

    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date endTime;

    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date startTime;

    private String message;

    private Integer orderId;

    @Column(columnDefinition = "Varchar(255) default Not Started")
    private String status="Not Started";

    private String suiteName;

    private String tags;

    private Integer suiteId;

    @Transient
    private JSONObject input;


    private String rollback;

    public String isRollback() {
        return rollback;
    }

    public void setRollback(String rollback) {
        this.rollback = rollback;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public JSONObject getInput() {
        return input;
    }

    public void setInput(JSONObject input) {
        this.input = input;
    }

    public Integer getBotId() {
        return botId;
    }

    public void setBotId(Integer botId) {
        this.botId = botId;
    }

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public String getCritical() {
        return isCritical;
    }

    public void setCritical(String critical) {
        isCritical = critical;
    }

    public Date getElapsed() {
        return elapsed;
    }

    public void setElapsed(Date elapsed) {
        this.elapsed = elapsed;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSuiteName() {
        return suiteName;
    }

    public void setSuiteName(String suiteName) {
        this.suiteName = suiteName;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getSuiteId() {
        return suiteId;
    }

    public void setSuiteId(Integer suiteId) {
        this.suiteId = suiteId;
    }

}
