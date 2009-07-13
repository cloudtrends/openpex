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

/**
 *
 * @author srikumar
 */
public class ReservationReply {
    
    public static enum ReservationReplyType{ACCEPT,REJECT,COUNTER};
    
    private ReservationReplyType reply;
    private ReservationProposal proposal;

    public ReservationProposal getProposal() {
        return proposal;
    }

    public void setProposal(ReservationProposal proposal) {
        this.proposal = proposal;
    }

    public ReservationReplyType getReply() {
        return reply;
    }

    public void setReply(ReservationReplyType reply) {
        this.reply = reply;
    }
    
}
