package org.prodapt.raf.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="SuiteDetails")
public class SuiteDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer suiteId;

    private String suiteName;

    @CreationTimestamp
    @Temporal(value = TemporalType.TIME)
    private Date elapsed;

    private Integer fail;

    private Integer pass;

    private Integer total;

    private Integer orderId;

    public Integer getSubOrderId() {
        return subOrderId;
    }

    public void setSubOrderId(Integer subOrderId) {
        this.subOrderId = subOrderId;
    }

    private Integer subOrderId;

    @OneToMany(cascade = CascadeType.ALL)
    private List<BotDetails> botDetails;

    public Integer getSuiteId() {
        return suiteId;
    }

    public void setSuiteId(Integer suiteId) {
        this.suiteId = suiteId;
    }

    public String getSuiteName() {
        return suiteName;
    }

    public void setSuiteName(String suiteName) {
        this.suiteName = suiteName;
    }

    public Date getElapsed() {
        return elapsed;
    }

    public void setElapsed(Date elapsed) {
        this.elapsed = elapsed;
    }

    public Integer getFail() {
        return fail;
    }

    public void setFail(Integer fail) {
        this.fail = fail;
    }

    public Integer getPass() {
        return pass;
    }

    public void setPass(Integer pass) {
        this.pass = pass;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public List<BotDetails> getBotDetails() {
        return botDetails;
    }

    public void setBotDetails(List<BotDetails> botDetails) {
        this.botDetails = botDetails;
    }
}
