<?xml version="1.0" encoding="UTF-8"?>
<!-- 
    Document   : Start
    Created on : Mar 18, 2009, 12:28:00 PM
    Author     : brobergj

    "Copyright 2008, 2009 Srikumar Venugopal & James Broberg"

    This file is part of OpenPEX.

    OpenPEX is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    OpenPEX is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with OpenPEX.  If not, see http://www.gnu.org/licenses/.
-->
<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:ice="http://www.icesoft.com/icefaces/component" xmlns:jsp="http://java.sun.com/JSP/Page">
    <jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>
    <f:view>
        <html id="outputHtml1">
            <head id="outputHead1">
                <ice:outputStyle href="./resources/stylesheet.css" id="outputStyle1"/>
                <ice:outputStyle href="./xmlhttp/css/xp/xp.css" id="outputStyle2"/>
            </head>
            <body id="outputBody1" style="-rave-layout: grid">
                <ice:form id="form1">
                    <ice:commandButton action="#{Start.newResButton_action}" id="newResButton" style="left: 24px; top: 48px; position: absolute; width: 240px" value="Make New Reservation"/>
                    <ice:commandButton action="#{Start.viewResButton_action}" id="viewResButton"
                        style="left: 432px; top: 48px; position: absolute; width: 240px" value="View / Activate Reservations"/>
                    <ice:commandButton action="#{Start.viewAccountButton_action}" disabled="true" id="viewAccountButton"
                        style="left: 24px; top: 96px; position: absolute; width: 240px" value="View / Edit PEX Account"/>
                    <ice:commandButton action="#{Start.viewVMButton_action}" id="viewVMButton" style="left: 432px; top: 96px; position: absolute; width: 240px" value="View / Halt Running VMs"/>
                    <ice:outputLabel id="outputLabel1" style="left: 24px; top: 0px; position: absolute" value="Welcome to the PEX Portal!"/>
                    <ice:outputText id="outputText1" style="left: 528px; top: 0px; position: absolute; width: 142px" value="#{SessionBean1.user.fullname}"/>
                </ice:form>
                <div style="left: 24px; top: 144px; position: absolute">
                    <jsp:directive.include file="footer.jspf"/>
                </div>
            </body>
        </html>
    </f:view>
</jsp:root>
