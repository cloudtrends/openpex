//    “Copyright 2008, 2009 Srikumar Venugopal & James Broberg”
//
//    This file is part of OpenPEX.
//
//    OpenPEX is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 2 of the License, or
//    (at your option) any later version.
//
//    OpenPEX is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with OpenPEX.  If not, see <http://www.gnu.org/licenses/>.

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
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.unimelb.openpex.ClusterNode;
import org.unimelb.openpex.Constants.ReservationStatus;
import org.unimelb.openpex.VMInstance;
import org.unimelb.openpex.VmUser;
import org.unimelb.openpex.reservation.ReservationProposal;

/**
 *
 * @author srikumar
 */
@Entity
@Table(name = "VM_RES")
@NamedQueries({@NamedQuery(name = "ReservationEntity.findByRequestId", query = "SELECT r FROM ReservationEntity r WHERE r.requestId = :requestId"),
    @NamedQuery(name = "ReservationEntity.findByNumInstances", query = "SELECT r FROM ReservationEntity r WHERE r.numInstances = :numInstances"),
    @NamedQuery(name = "ReservationEntity.findByStartTime", query = "SELECT r FROM ReservationEntity r WHERE r.startTime = :startTime"),
    @NamedQuery(name = "ReservationEntity.findByEndTime", query = "SELECT r FROM ReservationEntity r WHERE r.endTime = :endTime"),
    @NamedQuery(name = "ReservationEntity.findByCpus", query = "SELECT r FROM ReservationEntity r WHERE r.cpus = :cpus"),
    @NamedQuery(name = "ReservationEntity.findByTemplate", query = "SELECT r FROM ReservationEntity r WHERE r.template = :template"),
    @NamedQuery(name = "ReservationEntity.findByUserid", query = "SELECT r FROM ReservationEntity r WHERE r.userid = :userid"),
    @NamedQuery(name = "ReservationEntity.findByStatus", query = "SELECT r FROM ReservationEntity r WHERE r.status = :status")})
public class ReservationEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "request_id", nullable = false)
    private String requestId;
    @Column(name = "num_instances", nullable = true)
    private short numInstances;
    @Column(name = "start_time", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    @Column(name = "end_time", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;
    @Column(name = "cpus", nullable = true)
    private short cpus;
    @Column(name = "template", nullable = true)
    private String template;
    @Column(name = "status", nullable = true)
    private ReservationStatus status;
    @OneToMany(mappedBy = "reservation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<VMInstance> vmSet;
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "RES_NODES",
    joinColumns = @JoinColumn(name = "request_id", referencedColumnName = "request_id"),
    inverseJoinColumns = @JoinColumn(name = "node_name", referencedColumnName = "NODE_NAME"))
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

    public ReservationEntity(String requestId, short userid, short numInstances, Date startTime, Date endTime, short cpus, String template, ReservationStatus status) {
        this(requestId);
        this.numInstances = numInstances;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cpus = cpus;
        this.template = template;
        this.status = status;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public short getNumInstances() {
        return numInstances;
    }

    public void setNumInstances(short numInstances) {
        this.numInstances = numInstances;
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

    public short getCpus() {
        return cpus;
    }

    public void setCpus(short cpus) {
        this.cpus = cpus;
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

    public void incrementInstances() {
        activatedInstances++;
    }

    public void decrementInstances() {
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

    public ReservationProposal convertToProposal() {
        ReservationProposal proposal = new ReservationProposal();
        proposal.setId(requestId);
        Calendar start = Calendar.getInstance();
        start.setTime(startTime);
        proposal.setStartTime(start);
        proposal.setDuration(endTime.getTime() - startTime.getTime());
        proposal.setCPUs(cpus);
        proposal.setNumInstances(numInstances);
        proposal.setTemplate(template);
        proposal.setUserid(this.getUserid());
        return proposal;
    }

    public Object clone() {
        ReservationEntity entity = new ReservationEntity();
        entity.setRequestId(requestId);
        entity.setUserid(userid);
        entity.setCpus(cpus);
        entity.setStartTime((Date) startTime.clone());
        entity.setEndTime((Date) endTime.clone());
        entity.setNumInstances(numInstances);
        entity.setStatus(status);
        entity.setTemplate(template);
        entity.setVmSet(new HashSet<VMInstance>(vmSet));
        entity.setNodes(new HashSet<ClusterNode>(nodes));
        return entity;
    }
}
