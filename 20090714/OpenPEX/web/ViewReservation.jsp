<?xml version="1.0" encoding="UTF-8"?>
<!-- 
    Document   : ViewReservation
    Created on : Mar 18, 2009, 5:45:02 PM
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
                    <ice:outputLabel id="outputLabel1" style="left: 24px; top: 24px; position: absolute" value="View and Activate Reservations"/>
                    <ice:dataPaginator for="dataTable1" id="dataPaginator1" paginatorMaxPages="5" style="left: 14; top: 62; position: absolute; height: 24px;">
                        <f:facet name="first">
                            <ice:graphicImage id="graphicImage1" url="./xmlhttp/css/xp/css-images/arrow-first.gif"/>
                        </f:facet>
                        <f:facet name="last">
                            <ice:graphicImage id="graphicImage2" url="./xmlhttp/css/xp/css-images/arrow-last.gif"/>
                        </f:facet>
                        <f:facet name="previous">
                            <ice:graphicImage id="graphicImage3" url="./xmlhttp/css/xp/css-images/arrow-previous.gif"/>
                        </f:facet>
                        <f:facet name="next">
                            <ice:graphicImage id="graphicImage4" url="./xmlhttp/css/xp/css-images/arrow-next.gif"/>
                        </f:facet>
                        <f:facet name="fastforward">
                            <ice:graphicImage id="graphicImage5" url="./xmlhttp/css/xp/css-images/arrow-ff.gif"/>
                        </f:facet>
                        <f:facet name="fastrewind">
                            <ice:graphicImage id="graphicImage6" url="./xmlhttp/css/xp/css-images/arrow-fr.gif"/>
                        </f:facet>
                    </ice:dataPaginator>
                    <ice:dataTable id="dataTable1" style="left: 14px; top: 86px; position: absolute" value="#{ViewReservation.dataTable1Model}" var="currentRow">
                        <ice:column id="column6">
                            <ice:outputText id="outputText11" value="#{currentRow['template']}"/>
                            <f:facet name="header">
                                <ice:outputText id="outputText12" value="Template"/>
                            </f:facet>
                        </ice:column>
                        <ice:column id="column1">
                            <ice:outputText id="outputText1" value="#{currentRow['requestId']}"/>
                            <f:facet name="header">
                                <ice:outputText id="outputText2" value="Request ID"/>
                            </f:facet>
                        </ice:column>
                        <ice:column id="column2">
                            <ice:outputText id="outputText3" value="#{currentRow['numInstancesFixed']}"/>
                            <f:facet name="header">
                                <ice:outputText id="outputText4" value="# Instances"/>
                            </f:facet>
                        </ice:column>
                        <ice:column id="column3">
                            <ice:outputText id="outputText5" value="#{currentRow['startTime']}"/>
                            <f:facet name="header">
                                <ice:outputText id="outputText6" value="Start Time"/>
                            </f:facet>
                        </ice:column>
                        <ice:column id="column4">
                            <ice:outputText id="outputText7" value="#{currentRow['endTime']}"/>
                            <f:facet name="header">
                                <ice:outputText id="outputText8" value="End Time"/>
                            </f:facet>
                        </ice:column>
                        <ice:column id="column5">
                            <ice:outputText id="outputText9" value="#{currentRow['status']}"/>
                            <f:facet name="header">
                                <ice:outputText id="outputText10" value="Status"/>
                            </f:facet>
                        </ice:column>
                        <ice:column id="column7">
                            <ice:commandButton action="#{ViewReservation.activateButton_action}"
                                actionListener="#{ViewReservation.activateButton_processAction}" id="activateButton" value="Activate"/>
                            <f:facet name="header">
                                <ice:outputText id="outputText14" value="Activate"/>
                            </f:facet>
                        </ice:column>
                        <ice:column id="column8">
                            <ice:commandButton action="#{ViewReservation.deleteButton_action}" actionListener="#{ViewReservation.deleteButton_processAction}"
                                id="deleteButton" value="Delete"/>
                            <f:facet name="header">
                                <ice:outputText id="outputText13" value="Delete"/>
                            </f:facet>
                        </ice:column>
                    </ice:dataTable>
                    <ice:commandButton action="#{ViewReservation.backButton_action}" id="backButton" style="left: 24px; top: 360px; position: absolute" value="Back"/>
                </ice:form>
                <div style="left: 24px; top: 408px; position: absolute">
                    <jsp:directive.include file="footer.jspf"/>
                </div>
            </body>
        </html>
    </f:view>
</jsp:root>
