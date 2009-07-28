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
 * NewReservation.java
 *
 * Created on Mar 19, 2009, 3:30:31 PM
 * Copyright brobergj
 */
package org.unimelb.openpex.portal;

import com.icesoft.faces.component.jsfcl.data.DefaultSelectedData;
import com.icesoft.faces.component.jsfcl.data.DefaultSelectionItems;
import com.icesoft.faces.component.jsfcl.data.PopupBean;
import com.icesoft.faces.component.jsfcl.data.SelectInputDateBean;
import com.sun.rave.faces.data.DefaultSelectItemsArray;
import com.sun.rave.web.ui.appbase.AbstractPageBean;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import org.unimelb.openpex.PexException;
import org.unimelb.openpex.ResourceManager;
import org.unimelb.openpex.reservation.InstanceType;
import org.unimelb.openpex.reservation.PexReservationFailedException;
import org.unimelb.openpex.reservation.ReservationManager;
import org.unimelb.openpex.reservation.ReservationProposal;
import org.unimelb.openpex.reservation.ReservationReply;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class NewReservation extends AbstractPageBean {
    // <editor-fold defaultstate="collapsed" desc="Managed Component Definition">

    private int __placeholder;
    private List<String> templates;
    private String startHour;
    private String startMin;
    private String endHour;
    private String endMin;
    private String cpuReq;
    private String selectedTemplate;

    public String getSelectedTemplate() {
        return selectedTemplate;
    }

    public void setSelectedTemplate(String selectedTemplate) {
        this.selectedTemplate = selectedTemplate;
    }

    

    public String getEndHour() {
        return endHour;
    }

    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }

    public String getEndMin() {
        return endMin;
    }

    public void setEndMin(String endMin) {
        this.endMin = endMin;
    }

    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    public String getStartMin() {
        return startMin;
    }

    public void setStartMin(String startMin) {
        this.startMin = startMin;
    }

    public String getCpuReq() {
        return cpuReq;
    }

    public void setCpuReq(String cpuReq) {
        this.cpuReq = cpuReq;
    }

    public List<String> getTemplates() {
        return templates;
    }

    public void setTemplates(List<String> templates) {
        this.templates = templates;
    }

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
    }
    private SelectInputDateBean selectInputDate1Bean = new SelectInputDateBean();

    public SelectInputDateBean getSelectInputDate1Bean() {
        return selectInputDate1Bean;
    }

    public void setSelectInputDate1Bean(SelectInputDateBean sidb) {
        this.selectInputDate1Bean = sidb;
    }
    private SelectInputDateBean selectInputDate2Bean = new SelectInputDateBean();

    public SelectInputDateBean getSelectInputDate2Bean() {
        return selectInputDate2Bean;
    }

    public void setSelectInputDate2Bean(SelectInputDateBean sidb) {
        this.selectInputDate2Bean = sidb;
    }
    private DefaultSelectedData selectOneMenu1Bean = new DefaultSelectedData();

    public DefaultSelectedData getSelectOneMenu1Bean() {
        return selectOneMenu1Bean;
    }

    public void setSelectOneMenu1Bean(DefaultSelectedData dsd) {
        this.selectOneMenu1Bean = dsd;
    }
    private DefaultSelectionItems selectOneMenu1DefaultItems = new DefaultSelectionItems();

    public DefaultSelectionItems getSelectOneMenu1DefaultItems() {
        return selectOneMenu1DefaultItems;
    }

    public void setSelectOneMenu1DefaultItems(DefaultSelectionItems dsi) {
        this.selectOneMenu1DefaultItems = dsi;
    }
    private DefaultSelectedData selectOneMenu2Bean = new DefaultSelectedData();

    public DefaultSelectedData getSelectOneMenu2Bean() {
        return selectOneMenu2Bean;
    }

    public void setSelectOneMenu2Bean(DefaultSelectedData dsd) {
        this.selectOneMenu2Bean = dsd;
    }
    private DefaultSelectionItems selectOneMenu2DefaultItems = new DefaultSelectionItems();

    public DefaultSelectionItems getSelectOneMenu2DefaultItems() {
        return selectOneMenu2DefaultItems;
    }

    public void setSelectOneMenu2DefaultItems(DefaultSelectionItems dsi) {
        this.selectOneMenu2DefaultItems = dsi;
    }
    private DefaultSelectedData selectOneMenu3Bean = new DefaultSelectedData();

    public DefaultSelectedData getSelectOneMenu3Bean() {
        return selectOneMenu3Bean;
    }

    public void setSelectOneMenu3Bean(DefaultSelectedData dsd) {
        this.selectOneMenu3Bean = dsd;
    }
    private DefaultSelectionItems selectOneMenu3DefaultItems = new DefaultSelectionItems();

    public DefaultSelectionItems getSelectOneMenu3DefaultItems() {
        return selectOneMenu3DefaultItems;
    }

    public void setSelectOneMenu3DefaultItems(DefaultSelectionItems dsi) {
        this.selectOneMenu3DefaultItems = dsi;
    }
    private DefaultSelectedData selectOneMenu4Bean = new DefaultSelectedData();

    public DefaultSelectedData getSelectOneMenu4Bean() {
        return selectOneMenu4Bean;
    }

    public void setSelectOneMenu4Bean(DefaultSelectedData dsd) {
        this.selectOneMenu4Bean = dsd;
    }
    private DefaultSelectionItems selectOneMenu4DefaultItems = new DefaultSelectionItems();

    public DefaultSelectionItems getSelectOneMenu4DefaultItems() {
        return selectOneMenu4DefaultItems;
    }

    public void setSelectOneMenu4DefaultItems(DefaultSelectionItems dsi) {
        this.selectOneMenu4DefaultItems = dsi;
    }
    private DefaultSelectedData selectOneMenu5Bean = new DefaultSelectedData();

    public DefaultSelectedData getSelectOneMenu5Bean() {
        return selectOneMenu5Bean;
    }

    public void setSelectOneMenu5Bean(DefaultSelectedData dsd) {
        this.selectOneMenu5Bean = dsd;
    }
    private DefaultSelectionItems selectOneMenu5DefaultItems = new DefaultSelectionItems();

    public DefaultSelectionItems getSelectOneMenu5DefaultItems() {
        return selectOneMenu5DefaultItems;
    }

    public void setSelectOneMenu5DefaultItems(DefaultSelectionItems dsi) {
        this.selectOneMenu5DefaultItems = dsi;
    }
    private DefaultSelectedData selectOneMenu6Bean = new DefaultSelectedData();

    public DefaultSelectedData getSelectOneMenu6Bean() {
        return selectOneMenu6Bean;
    }

    public void setSelectOneMenu6Bean(DefaultSelectedData dsd) {
        this.selectOneMenu6Bean = dsd;
    }
    private DefaultSelectionItems selectOneMenu6DefaultItems = new DefaultSelectionItems();

    public DefaultSelectionItems getSelectOneMenu6DefaultItems() {
        return selectOneMenu6DefaultItems;
    }

    public void setSelectOneMenu6DefaultItems(DefaultSelectionItems dsi) {
        this.selectOneMenu6DefaultItems = dsi;
    }
    private PopupBean panelPopup1Bean = new PopupBean();

    public PopupBean getPanelPopup1Bean() {
        return panelPopup1Bean;
    }

    public void setPanelPopup1Bean(PopupBean pb) {
        this.panelPopup1Bean = pb;
    }

    // </editor-fold>
    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public NewReservation() {
    }

    /**
     * <p>Callback method that is called whenever a page is navigated to,
     * either directly via a URL, or indirectly via page navigation.
     * Customize this method to acquire resources that will be needed
     * for event handlers and lifecycle methods, whether or not this
     * page is performing post back processing.</p>
     * 
     * <p>Note that, if the current request is a postback, the property
     * values of the components do <strong>not</strong> represent any
     * values submitted with this request.  Instead, they represent the
     * property values that were saved for this view when it was rendered.</p>
     */
    @Override
    public void init() {
        // Perform initializations inherited from our superclass
        super.init();
        // Perform application initialization that must complete
        // *before* managed components are initialized
        // TODO - add your own initialiation code here

        // <editor-fold defaultstate="collapsed" desc="Managed Component Initialization">
        // Initialize automatically managed components
        // *Note* - this logic should NOT be modified
        try {
            _init();
        } catch (Exception e) {
            log("NewReservation Initialization Failure", e);
            throw e instanceof FacesException ? (FacesException) e : new FacesException(e);
        }

        // </editor-fold>
        // Perform application initialization that must complete
        // *after* managed components are initialized
        // TODO - add your own initialization code here
        ResourceManager resM = null;
        List<String> templates_ = null;
        String[] cpus = {"1", "2", "3", "4"};

        String[] hours = {"00", "01", "02", "03", "04", "05", "06",
            "07", "08", "09", "10", "11", "12",
            "13", "14", "15", "16", "17", "18",
            "19", "20", "21", "22", "23"};
        String[] mins = {"00", "05", "10", "15", "20",
            "25", "30", "35", "40", "45",
            "50", "55"};

        try {
            resM = ResourceManager.getInstance();
            templates_ = resM.getPexTemplateStrings();

            Logger.getLogger(NewReservation.class.getName()).log(Level.SEVERE, "Found " +
                    templates_.size() + " templates");

            this.setTemplates(templates_);

        //rm = ResourceManager.getInstance();
        } catch (PexException ex) {
            Logger.getLogger(NewReservation.class.getName()).log(Level.SEVERE, null, ex);
        }

        DefaultSelectionItems dsi1 = new DefaultSelectionItems();
        dsi1.setItems(cpus);
        this.setSelectOneMenu1DefaultItems(dsi1);

        DefaultSelectionItems dsi2 = new DefaultSelectionItems();
        dsi2.setItems((String[]) this.getTemplates().toArray(new String[0]));
        this.setSelectOneMenu2DefaultItems(dsi2);

        DefaultSelectionItems dsiHours = new DefaultSelectionItems();
        dsiHours.setItems(hours);
        this.setSelectOneMenu3DefaultItems(dsiHours);
        this.setSelectOneMenu5DefaultItems(dsiHours);

        DefaultSelectionItems dsiMins = new DefaultSelectionItems();
        dsiMins.setItems(mins);
        this.setSelectOneMenu4DefaultItems(dsiMins);
        this.setSelectOneMenu6DefaultItems(dsiMins);



    }

    /**
     * <p>Callback method that is called after the component tree has been
     * restored, but before any event processing takes place.  This method
     * will <strong>only</strong> be called on a postback request that
     * is processing a form submit.  Customize this method to allocate
     * resources that will be required in your event handlers.</p>
     */
    @Override
    public void preprocess() {
    }

    /**
     * <p>Callback method that is called just before rendering takes place.
     * This method will <strong>only</strong> be called for the page that
     * will actually be rendered (and not, for example, on a page that
     * handled a postback and then navigated to a different page).  Customize
     * this method to allocate resources that will be required for rendering
     * this page.</p>
     */
    @Override
    public void prerender() {
    }

    /**
     * <p>Callback method that is called after rendering is completed for
     * this request, if <code>init()</code> was called (regardless of whether
     * or not this was the page that was actually rendered).  Customize this
     * method to release resources acquired in the <code>init()</code>,
     * <code>preprocess()</code>, or <code>prerender()</code> methods (or
     * acquired during execution of an event handler).</p>
     */
    @Override
    public void destroy() {
    }

    /**
     * <p>Return a reference to the scoped data bean.</p>
     *
     * @return reference to the scoped data bean
     */
    protected RequestBean1 getRequestBean1() {
        return (RequestBean1) getBean("RequestBean1");
    }

    /**
     * <p>Return a reference to the scoped data bean.</p>
     *
     * @return reference to the scoped data bean
     */
    protected ApplicationBean1 getApplicationBean1() {
        return (ApplicationBean1) getBean("ApplicationBean1");
    }

    /**
     * <p>Return a reference to the scoped data bean.</p>
     *
     * @return reference to the scoped data bean
     */
    protected SessionBean1 getSessionBean1() {
        return (SessionBean1) getBean("SessionBean1");
    }

    public void selectOneMenu1_processValueChange(ValueChangeEvent vce) {
    }

    public void button1_processAction(ActionEvent ae) {
        try {
            SessionBean1 sessBean = getSessionBean1();
            Calendar beginTime = Calendar.getInstance();
            beginTime.setTime(getSelectInputDate1Bean().getDate1());
            beginTime.add(Calendar.HOUR, Integer.parseInt(this.getStartHour()));
            beginTime.add(Calendar.MINUTE, Integer.parseInt(this.getStartMin()));
            Calendar endTime = Calendar.getInstance();
            endTime.setTime(getSelectInputDate2Bean().getDate1());
            endTime.add(Calendar.HOUR, Integer.parseInt(this.getEndHour()));
            endTime.add(Calendar.MINUTE, Integer.parseInt(this.getEndMin()));
            System.out.println("You have requested a lease that begins at " + beginTime.getTime().toString());
            System.out.println("You have requested a lease that ends at " + endTime.getTime().toString());
            long duration = endTime.getTimeInMillis() - beginTime.getTimeInMillis();

            // If the duration is less than 0 then barf
            if (duration < 0) {
                System.out.println("Your reservation end time is before the start time!");
                return;
            }

            System.out.println("The duration of your request is approximately " + duration / 1000 / 60 / 60 + " hours");
            int cpuReq_ = Integer.parseInt(this.getCpuReq());
            System.out.println("You requested " + cpuReq_ + " CPU(s)");
            ReservationManager rm = null;

            try {
                rm = ResourceManager.getInstance();
            } catch (PexException ex) {
                Logger.getLogger(NewReservation.class.getName()).log(Level.SEVERE, null, ex);
            }

            Logger.getLogger(SessionBean1.class.getName()).log(Level.SEVERE, "From Reserve page " + new Long(rm.getUniqueIdentifier()).toString());
            String resID = rm.initiateReservation(sessBean.getUser().getUserid());
            ReservationProposal proposal1 = new ReservationProposal(resID);
            proposal1.setTemplate(this.getSelectedTemplate());
            proposal1.setType(InstanceType.SMALL);
            proposal1.setNumInstancesFixed(1);
            proposal1.setStartTime(beginTime.getTime());
            proposal1.setDuration(duration);
            proposal1.setUserid(sessBean.getUser().getUserid());
            try {
                ReservationReply reply = rm.requestReservation(resID, proposal1);
                if (reply.getReply() == ReservationReply.ReservationReplyType.ACCEPT) {
                    error("Reservation Accepted");
                    rm.confirmReservation(resID, proposal1);
                } else if (reply.getReply() == ReservationReply.ReservationReplyType.COUNTER) {
                    Logger.getLogger(SessionBean1.class.getName()).log(Level.INFO, "Counter " + reply.getProposal().getStartTime().toString() + " " + reply.getProposal().getDuration());
                    sessBean.setReply(reply);
                    return;
                } else {
                    error("Reservation Not Accepted");
                }
//			resM.startVM(resID1, "bloi");
            } catch (PexException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (PexReservationFailedException ex) {
            Logger.getLogger(NewReservation.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String button1_action() {
        //return null means stay on the same page
        return null;
    }

    public String button2_action() {
        //return null means stay on the same page
        return "case1";
    }
}

