package org.prodapt.raf.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="NodeDetails")
public class NodeDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer nodeId;

    private Integer orderId;

    private String nodeName;

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

    private String status;

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
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

}
