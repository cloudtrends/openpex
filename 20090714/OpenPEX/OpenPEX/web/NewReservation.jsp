<?xml version="1.0" encoding="UTF-8"?>
<!-- 
    Document   : NewReservation
    Created on : Mar 19, 2009, 3:30:30 PM
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
                    <ice:outputText id="outputText1" style="left: 24px; top: 24px; position: absolute" value="Make a new reservation:"/>
                    <ice:outputLabel id="outputLabel1" style="left: 24px; top: 72px; position: absolute" value="Start date:"/>
                    <ice:selectInputDate id="selectInputDate1" renderAsPopup="true"
                        style="height: 212px; left: 120px; top: 72px; position: absolute; width: 190px" value="#{NewReservation.selectInputDate1Bean.date1}"/>
                    <ice:outputText id="outputText2" style="left: 432px; top: 72px; position: absolute" value="End date:"/>
                    <ice:selectInputDate id="selectInputDate2" renderAsPopup="true"
                        style="height: 212px; left: 504px; top: 72px; position: absolute; width: 190px" value="#{NewReservation.selectInputDate2Bean.date1}"/>
                    <ice:outputLabel id="outputLabel2" style="left: 24px; top: 192px; position: absolute; width: 72px" value="Start time:"/>
                    <ice:outputLabel id="outputLabel3" style="left: 408px; top: 192px; position: absolute; width: 72px" value="End time:"/>
                    <ice:outputLabel id="outputLabel4"
                        style="left: 216px; top: 192px; position: absolute; text-align: center; vertical-align: middle; width: 24px" value=":"/>
                    <ice:outputLabel id="outputLabel5"
                        style="left: 576px; top: 192px; position: absolute; text-align: center; vertical-align: middle; width: 24px" value=":"/>
                    <ice:outputLabel id="outputLabel6" style="left: 24px; top: 288px; position: absolute; width: 68px" value="CPUs:"/>
                    <ice:selectOneMenu id="selectOneMenu1" partialSubmit="true" style="left: 118px; top: 286px; position: absolute"
                        value="#{NewReservation.cpuReq}" valueChangeListener="#{NewReservation.selectOneMenu1_processValueChange}">
                        <f:selectItems id="selectOneMenu1selectItems" value="#{NewReservation.selectOneMenu1DefaultItems}"/>
                    </ice:selectOneMenu>
                    <ice:outputLabel id="outputLabel7" style="left: 24px; top: 336px; position: absolute; width: 72px" value="Template:"/>
                    <ice:selectOneMenu id="selectOneMenu2" partialSubmit="true" style="left: 118px; top: 334px; position: absolute; width: 192px" value="#{NewReservation.selectedTemplate}">
                        <f:selectItems id="selectOneMenu2selectItems" value="#{NewReservation.selectOneMenu2DefaultItems}"/>
                    </ice:selectOneMenu>
                    <ice:commandButton action="#{NewReservation.button1_action}" actionListener="#{NewReservation.button1_processAction}" id="button1"
                        style="left: 24px; top: 408px; position: absolute; width: 96px" value="OK"/>
                    <ice:commandButton action="#{NewReservation.button2_action}" id="button2" style="left: 144px; top: 408px; position: absolute; width: 96px" value="Back"/>
                    <ice:selectOneMenu id="selectOneMenu3" partialSubmit="true" style="left: 118px; top: 190px; position: absolute" value="#{NewReservation.startHour}">
                        <f:selectItems id="selectOneMenu3selectItems" value="#{NewReservation.selectOneMenu3DefaultItems}"/>
                    </ice:selectOneMenu>
                    <ice:selectOneMenu id="selectOneMenu4" partialSubmit="true" style="left: 238px; top: 190px; position: absolute" value="#{NewReservation.startMin}">
                        <f:selectItems id="selectOneMenu4selectItems" value="#{NewReservation.selectOneMenu4DefaultItems}"/>
                    </ice:selectOneMenu>
                    <ice:selectOneMenu id="selectOneMenu5" partialSubmit="true" style="left: 478px; top: 190px; position: absolute" value="#{NewReservation.endHour}">
                        <f:selectItems id="selectOneMenu5selectItems" value="#{NewReservation.selectOneMenu5DefaultItems}"/>
                    </ice:selectOneMenu>
                    <ice:selectOneMenu id="selectOneMenu6" partialSubmit="true" style="left: 598px; top: 190px; position: absolute" value="#{NewReservation.endMin}">
                        <f:selectItems id="selectOneMenu6selectItems" value="#{NewReservation.selectOneMenu6DefaultItems}"/>
                    </ice:selectOneMenu>
                    <ice:panelPopup draggable="true" id="panelPopup1" rendered="#{NewReservation.panelPopup1Bean.showDraggablePanel}"
                        style="display: block; left: 216px; top: 72px; position: absolute; width: 400px" visible="#{NewReservation.panelPopup1Bean.showModalPanel}">
                        <f:facet name="header">
                            <ice:panelGrid id="panelGrid1" style="display:block;width:380px;height:20px;">
                                <ice:outputText id="outputText3" value="Alternate Offer"/>
                            </ice:panelGrid>
                        </f:facet>
                        <f:facet name="body">
                            <ice:panelGrid id="panelGrid2" style="display:block;width:380px;height:200px;" columns="2">
                                <ice:outputLabel id="outputLabel8" value="outputLabel"/>
                                <ice:outputText id="outputText4" value="outputText"/>
                                <ice:outputLabel id="outputLabel9" value="outputLabel"/>
                                <ice:outputText id="outputText5" value="outputText"/>
                            </ice:panelGrid>
                        </f:facet>
                    </ice:panelPopup>
                </ice:form>
                <div style="left: 24px; top: 456px; position: absolute">
                    <jsp:directive.include file="footer.jspf"/>
                </div>
            </body>
        </html>
    </f:view>
</jsp:root>
