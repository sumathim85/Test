package org.prodapt.raf.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.List;


@Entity
@Table(name="OrderDetails")
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderCreatedDate;

    private String orderCreatedBy;

    private String confType;

    private String team;

    private String serviceType;

    private String orderType;

    private Integer subOrderId;

    @CreationTimestamp
    @Temporal(TemporalType.TIME)
    private Date elapsed;

    private Integer total;

    private Integer pass;

    private Integer fail;

    private String orderStatus;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SuiteDetails> suiteDetails;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="orderId")
    private List<SubOrderDetails> subOrderDetails;

    private int schedulerId;

    private String isScheduled;

    private String schedulerName;

    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public int getSchedulerId() {
        return schedulerId;
    }

    public void setSchedulerId(int schedulerId) {
        this.schedulerId = schedulerId;
    }

    public String getIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(String isScheduled) {
        this.isScheduled = isScheduled;
    }

    public List<FlowDetails> getFlowDetails() {
        return flowDetails;
    }

    public void setFlowDetails(List<FlowDetails> flowDetails) {
        this.flowDetails = flowDetails;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="orderId")
    private List<FlowDetails> flowDetails;

    public List<SubOrderDetails> getSubOrderDetails() {
        return subOrderDetails;
    }

    public void setSubOrderDetails(List<SubOrderDetails> subOrderDetails) {
        this.subOrderDetails = subOrderDetails;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Date getOrderCreatedDate() {
        return orderCreatedDate;
    }

    public void setOrderCreatedDate(Date orderCreatedDate) {
        this.orderCreatedDate = orderCreatedDate;
    }

    public String getOrderCreatedBy() {
        return orderCreatedBy;
    }

    public void setOrderCreatedBy(String orderCreatedBy) {
        this.orderCreatedBy = orderCreatedBy;
    }

    public String getConfType() {
        return confType;
    }

    public void setConfType(String confType) {
        this.confType = confType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Integer getSubOrderId() {
        return subOrderId;
    }

    public void setSubOrderId(Integer subOrderId) {
        this.subOrderId = subOrderId;
    }

    public Date getElapsed() {
        return elapsed;
    }

    public void setElapsed(Date elapsed) {
        this.elapsed = (Time) elapsed;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPass() {
        return pass;
    }

    public void setPass(Integer pass) {
        this.pass = pass;
    }

    public Integer getFail() {
        return fail;
    }

    public void setFail(Integer fail) {
        this.fail = fail;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    public String getServiceType() {  return serviceType;    }

    public void setServiceType(String serviceType) {   this.serviceType = serviceType;    }


    public List<SuiteDetails> getSuiteDetails() {
        return suiteDetails;
    }

    public void setSuiteDetails(List<SuiteDetails> suiteDetails) {
        this.suiteDetails = suiteDetails;
    }

}
