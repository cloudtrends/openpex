<?xml version="1.0" encoding="UTF-8"?>
<!-- 
    Document   : RegisterAccount
    Created on : Jan 21, 2009, 1:29:24 PM
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
                    <ice:outputLabel id="regOutputLabel" style="left: 24px; top: 24px; position: absolute" value="Register PEX Account"/>
                    <ice:outputLabel for="usernameInputText" id="outputLabel1" style="left: 24px; top: 72px; position: absolute" value="Username:"/>
                    <ice:outputLabel id="pass1OutputLabel" style="left: 24px; top: 120px; position: absolute" value="Password:"/>
                    <ice:outputLabel id="pass2OutputLabel" style="left: 24px; top: 168px; position: absolute" value="Re-enter Password:"/>
                    <ice:outputLabel id="nameOutputLabel" style="left: 24px; top: 216px; position: absolute" value="Full Name:"/>
                    <ice:outputLabel id="emailOutputLabel" style="left: 24px; top: 264px; position: absolute" value="Email:"/>
                    <ice:inputText action="#{RegisterAccount.usernameInputText_action}" id="usernameInputText" required="true"
                        requiredMessage="Please enter a username!" style="left: 166px; top: 70px; position: absolute"
                        validatorMessage="The username you entered is invalid!" value="#{RegisterAccount.usernameInputText}"/>
                    <ice:inputSecret id="pass1InputSecret" redisplay="true" style="left: 166px; top: 118px; position: absolute" value="#{RegisterAccount.pass1InputSecret}"/>
                    <ice:inputSecret id="pass2InputSecret" redisplay="true" style="left: 166px; top: 166px; position: absolute" value="#{RegisterAccount.pass2InputSecret}"/>
                    <ice:inputText id="nameInputText" style="left: 166px; top: 214px; position: absolute" value="#{RegisterAccount.nameInputText}"/>
                    <ice:inputText action="#{RegisterAccount.emailInputText_action}" id="emailInputText" style="left: 166px; top: 262px; position: absolute" value="#{RegisterAccount.emailInputText}"/>
                    <ice:commandButton action="#{RegisterAccount.regButton_action}" id="regButton" style="left: 24px; top: 336px; position: absolute" value="Register"/>
                    <ice:commandButton action="#{RegisterAccount.clearButton_action}" id="clearButton" style="left: 168px; top: 336px; position: absolute" value="Clear Fields"/>
                    <div style="left: 24px; top: 384px; position: absolute">
                        <jsp:directive.include file="footer.jspf"/>
                    </div>
                    <ice:messages errorClass="errorMessage" fatalClass="fatalMessage" id="messages1" infoClass="infoMessage" showSummary="true"
                        style="height: 214px; left: 384px; top: 72px; position: absolute; width: 262px" warnClass="warnMessage"/>
                </ice:form>
            </body>
        </html>
    </f:view>
</jsp:root>
