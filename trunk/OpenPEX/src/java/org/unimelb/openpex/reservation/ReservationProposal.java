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

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author srikumar
 */
public class ReservationProposal {

    private String id = "";
    private int numInstances = 0;
    private Calendar startTime = null;
    private long duration = 0;
    private int CPUs = 0;
    private int userid = 0;
    private String template = "";

    public ReservationProposal() {
    }

    public ReservationProposal(String id) {
        this.id = id;
    }

    protected ReservationProposal(ReservationProposal request) {
        this.id = request.getId();
        this.numInstances = request.getNumInstances();
        this.startTime = request.getStartTime();
        this.duration = request.getDuration();
        this.CPUs = request.getCPUs();
        this.template = request.getTemplate();
        this.userid = request.getUserid();
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getNumInstances() {
        return numInstances;
    }

    public void setNumInstances(int nodes) {
        this.numInstances = nodes;
    }

    public int getCPUs() {
        return CPUs;
    }

    public void setCPUs(int cpus) {
        this.CPUs = cpus;
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

    public Object clone() {
        return new ReservationProposal(this);
    }

    public ReservationEntity convertToEntity() {
        ReservationEntity re = new ReservationEntity(this.getId());
        re.setCpus((short) this.getCPUs());
        re.setNumInstances((short) this.getNumInstances());
        re.setTemplate(this.getTemplate());
        re.setStartTime(this.getStartTime().getTime());
        Date endTime = new Date();
        endTime.setTime(this.getStartTime().getTimeInMillis() + (long) this.getDuration());
        re.setEndTime(endTime);
        return re;

    }
}

