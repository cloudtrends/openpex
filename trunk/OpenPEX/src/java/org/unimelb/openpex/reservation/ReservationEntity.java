/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unimelb.openpex.reservation;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.unimelb.openpex.ClusterNode;
import org.unimelb.openpex.Constants.ReservationStatus;
import org.unimelb.openpex.VMInstance;

/**
 *
 * @author srikumar
 */
@Entity
@Table(name = "VM_RES")
@NamedQueries({@NamedQuery(name = "ReservationEntity.findByRequestId", query = "SELECT r FROM ReservationEntity r WHERE r.requestId = :requestId"),
        @NamedQuery(name = "ReservationEntity.findByUserid", query = "SELECT r FROM ReservationEntity r WHERE r.userid = :userid"),
        @NamedQuery(name = "ReservationEntity.findByNumInstances", query = "SELECT r FROM ReservationEntity r WHERE r.numInstancesFixed = :numInstancesFixed"),
        @NamedQuery(name = "ReservationEntity.findByStartTime", query = "SELECT r FROM ReservationEntity r WHERE r.startTime = :startTime"), 
        @NamedQuery(name = "ReservationEntity.findByEndTime", query = "SELECT r FROM ReservationEntity r WHERE r.endTime = :endTime"), 
        @NamedQuery(name = "ReservationEntity.findByTemplate", query = "SELECT r FROM ReservationEntity r WHERE r.template = :template"), 
        @NamedQuery(name = "ReservationEntity.findByStatus", query = "SELECT r FROM ReservationEntity r WHERE r.status = :status")})
        
public class ReservationEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "request_id", nullable = false)
    private String requestId;
    @Column(name = "num_instances_fixed", nullable = true)
    private short numInstancesFixed;
    @Column(name = "num_instances_option", nullable = true)
    private short numInstancesOption;
    @Column(name = "start_time", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    @Column(name = "end_time", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;
    @Column(name = "type", nullable = true)
    @Enumerated(EnumType.STRING)
    private InstanceType type;
    @Column(name = "template", nullable = true)
    private String template;
    @Column(name = "status", nullable = true)
    private ReservationStatus status;
    @OneToMany(mappedBy="reservation", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    private Set<VMInstance> vmSet;
    @ManyToMany(cascade=CascadeType.PERSIST)
    @JoinTable(name="RES_NODES",
        joinColumns=@JoinColumn(name="request_id", referencedColumnName="request_id"),
        inverseJoinColumns=@JoinColumn(name="node_name", referencedColumnName="NODE_NAME")
    )
    private Set<ClusterNode> nodes;
    @Column(name = "userid", nullable = false)
    private short userid;
    
    private transient int activatedInstances = 0;
    
    public ReservationEntity() {
    }

    public ReservationEntity(String requestId) {
        this.requestId = requestId;
        nodes = new HashSet<ClusterNode>();
        vmSet = new HashSet<VMInstance>();
    }

    public ReservationEntity(String requestId, short userid, short numInstancesFixed, short numInstancesOption, Date startTime, Date endTime, InstanceType type, String template, ReservationStatus status) {
        this(requestId);
        this.numInstancesFixed = numInstancesFixed;
        this.numInstancesOption = numInstancesOption;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.template = template;
        this.status = status;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public short getNumInstancesFixed() {
        return numInstancesFixed;
    }

    public void setNumInstancesFixed(short numInstancesFixed) {
        this.numInstancesFixed = numInstancesFixed;
    }

    public short getNumInstancesOption() {
        return numInstancesOption;
    }

    public void setNumInstancesOption(short numInstancesOption) {
        this.numInstancesOption = numInstancesOption;
    }

    public InstanceType getType() {
        return type;
    }

    public void setType(InstanceType type) {
        this.type = type;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public int getActivatedInstances() {
        return activatedInstances;
    }

    public void setActivatedInstances(int activatedInstances) {
        this.activatedInstances = activatedInstances;
    }
    
    public void incrementInstances(){
            activatedInstances++;
    }

    public void decrementInstances(){
            activatedInstances--;
    }

    public Set<ClusterNode> getNodes() {
        return nodes;
    }

    public void setNodes(Set<ClusterNode> nodes) {
        this.nodes = nodes;
    }
    
    public Set<VMInstance> getVmSet() {
        return vmSet;
    }

    public void setVmSet(Set<VMInstance> vmSet) {
        this.vmSet = vmSet;
    }

    public short getUserid() {
        return userid;
    }

    public void setUserid(short userid) {
        this.userid = userid;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (requestId != null ? requestId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ReservationEntity)) {
            return false;
        }
        ReservationEntity other = (ReservationEntity) object;
        if ((this.requestId == null && other.requestId != null) || 
                (this.requestId != null && !this.requestId.equals(other.requestId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.unimelb.pex.storage.ReservationEntity[requestId=" + requestId + "]";
    }
    
    public ReservationProposal convertToProposal(){
        ReservationProposal proposal = new ReservationProposal();
        proposal.setId(requestId);
        Calendar start = Calendar.getInstance();
        start.setTime(startTime);
        proposal.setStartTime(start.getTime());
        proposal.setDuration(endTime.getTime() - startTime.getTime());
        proposal.setType(type);
        proposal.setNumInstancesFixed(numInstancesFixed);
        proposal.setNumInstancesFixed(numInstancesOption);
        proposal.setTemplate(template);
        proposal.setUserid(this.getUserid());
        return proposal;
    }
    
    public Object clone(){
        ReservationEntity entity = new ReservationEntity();
        entity.setRequestId(requestId);
        entity.setUserid(userid);
        entity.setType(type);
        entity.setStartTime((Date)startTime.clone());
        entity.setEndTime((Date)endTime.clone());
        entity.setNumInstancesFixed(numInstancesFixed);
        entity.setNumInstancesOption(numInstancesOption);
        entity.setStatus(status);
        entity.setTemplate(template);
        entity.setVmSet(new HashSet<VMInstance>(vmSet));
        entity.setNodes(new HashSet<ClusterNode>(nodes));
        return entity;
    }
}
