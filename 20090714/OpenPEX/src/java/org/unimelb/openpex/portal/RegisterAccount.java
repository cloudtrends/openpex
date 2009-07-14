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
 * RegisterAccount.java
 *
 * Created on Jan 21, 2009, 1:29:25 PM
 * Copyright brobergj
 */
package org.unimelb.openpex.portal;

import com.icesoft.faces.component.ext.HtmlInputSecret;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.sun.data.provider.RowKey;
import com.sun.data.provider.impl.CachedRowSetDataProvider;
import com.sun.rave.web.ui.appbase.AbstractPageBean;
import javax.faces.FacesException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import org.unimelb.openpex.VmUser;
import org.unimelb.openpex.storage.PexStorage;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class RegisterAccount extends AbstractPageBean {
    // <editor-fold defaultstate="collapsed" desc="Managed Component Definition">

    private int __placeholder;

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {

    }
    private CachedRowSetDataProvider vm_userDataProvider = new CachedRowSetDataProvider();

    public CachedRowSetDataProvider getVm_userDataProvider() {
        return vm_userDataProvider;
    }

    public void setVm_userDataProvider(CachedRowSetDataProvider crsdp) {
        this.vm_userDataProvider = crsdp;
    }
    private String usernameInputText;

    public String getUsernameInputText() {
        return usernameInputText;
    }

    public void setUsernameInputText(String hit) {
        this.usernameInputText = hit;
    }
    private String pass1InputSecret;

    public String getPass1InputSecret() {
        return pass1InputSecret;
    }

    public void setPass1InputSecret(String his) {
        this.pass1InputSecret = his;
    }
    private String pass2InputSecret;

    public String getPass2InputSecret() {
        return pass2InputSecret;
    }

    public void setPass2InputSecret(String his) {
        this.pass2InputSecret = his;
    }
    private String nameInputText;

    public String getNameInputText() {
        return nameInputText;
    }

    public void setNameInputText(String hit) {
        this.nameInputText = hit;
    }
    private String emailInputText;

    public String getEmailInputText() {
        return emailInputText;
    }

    public void setEmailInputText(String hit) {
        this.emailInputText = hit;
    }

    // </editor-fold>
    /**
     * <p>Construct a new Page bean instance.</p>
     */
    public RegisterAccount() {
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
            log("RegisterAccount Initialization Failure", e);
            throw e instanceof FacesException ? (FacesException) e : new FacesException(e);
        }

    // </editor-fold>
    // Perform application initialization that must complete
    // *after* managed components are initialized
    // TODO - add your own initialization code here
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
        vm_userDataProvider.close();
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

    public String regButton_action() {
        try {
            PexStorage store = this.getSessionBean1().getStore();
            VmUser user = new VmUser(getUsernameInputText(),
                    getPass1InputSecret(), getNameInputText(),
                    getEmailInputText());
            store.saveUser(user);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        return "case1";
    }

    public String usernameInputText_action() {
        //return null means stay on the same page
        return null;
    }

    public void pass2InputSecret_processAction(ActionEvent ae) {
    }

    public String emailInputText_action() {
        //return null means stay on the same page
        return null;
    }

    public String clearButton_action() {
        //return null means stay on the same page
        return null;
    }
}

