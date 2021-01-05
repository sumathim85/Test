package org.prodapt.raf.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="SubOrderDetails")
public class SubOrderDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private Integer orderId;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderCreatedDate;

    private String orderCreatedBy;

    private String confType;

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

    public String getServiceType() { return serviceType;  }

    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

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

    public List<SuiteDetails> getSuiteDetails() {
        return suiteDetails;
    }

    public void setSuiteDetails(List<SuiteDetails> suiteDetails) {
        this.suiteDetails = suiteDetails;
    }
}
