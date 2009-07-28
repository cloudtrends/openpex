/*
 * Copyright Srikumar Venugopal and James Broberg 2009
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unimelb.openpex.reservation;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author srikumar
 */
public class ReservationProposal {

    private String id = "";
    private int numInstancesFixed = 0;
    private int numInstancesOption = 0;
    private Date startTime = null;
    private long duration = 0;
    private InstanceType type;
    private int userid = 0;
    private String template = "";


    public ReservationProposal() {
    }

    public ReservationProposal(String id) {
        this.id = id;
    }

    protected ReservationProposal(ReservationProposal request) {
        this.id = request.getId();
        this.numInstancesFixed = request.getNumInstancesFixed();
        this.startTime = request.getStartTime();
        this.duration = request.getDuration();
        this.template = request.getTemplate();
        this.userid = request.getUserid();
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getNumInstancesFixed() {
        return numInstancesFixed;
    }

    public void setNumInstancesFixed(int nodes) {
        this.numInstancesFixed = nodes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getNumInstancesOption() {
        return numInstancesOption;
    }

    public void setNumInstancesOption(int numInstancesOption) {
        this.numInstancesOption = numInstancesOption;
    }

    public InstanceType getType() {
        return type;
    }

    public void setType(InstanceType type) {
        this.type = type;
    }

    public Object clone() {
        return new ReservationProposal(this);
    }

    public ReservationEntity convertToEntity() {
        ReservationEntity re = new ReservationEntity(this.getId());
        re.setType(this.type);
        re.setNumInstancesFixed((short)numInstancesFixed);
        re.setNumInstancesOption((short)numInstancesOption);
        re.setTemplate(this.getTemplate());
        re.setStartTime(this.getStartTime());
        Date endTime = new Date();
        endTime.setTime(this.getStartTime().getTime() + (long) this.getDuration());
        re.setEndTime(endTime);
        return re;

    }
}

