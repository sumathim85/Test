package org.prodapt.raf.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="FlowDetails")
public class FlowDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer flowId;

    private Integer orderId;

    private String flowName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="orderId")
    private List<NodeDetails> nodeDetails;

    private Integer successNode;

    private Integer totalNode;

    private String triggerType;

    private Integer notExecutedNode;

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }


    public Integer getFlowId() {
        return flowId;
    }

    public void setFlowId(Integer flowId) {
        this.flowId = flowId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public List<NodeDetails> getNodeDetails() {
        return nodeDetails;
    }

    public void setNodeDetails(List<NodeDetails> nodeDetails) {
        this.nodeDetails = nodeDetails;
    }

    public Integer getSuccessNode() {
        return successNode;
    }

    public void setSuccessNode(Integer successNode) {
        this.successNode = successNode;
    }

    public Integer getTotalNode() {
        return totalNode;
    }

    public void setTotalNode(Integer totalNode) {
        this.totalNode = totalNode;
    }

    public Integer getNotExecutedNode() {
        return notExecutedNode;
    }

    public void setNotExecutedNode(Integer notExecutedNode) {
        this.notExecutedNode = notExecutedNode;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String Status;

    private String description;



}
