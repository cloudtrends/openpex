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

package org.unimelb.openpex.test;

import java.util.Calendar;

import org.unimelb.openpex.Bootstrap;
import org.unimelb.openpex.PexException;
import org.unimelb.openpex.ResourceManager;
import org.unimelb.openpex.reservation.ReservationManager;
import org.unimelb.openpex.reservation.ReservationProposal;
import org.unimelb.openpex.reservation.ReservationReply;

public class TestBootstrap {

    public static void main(String[] args) {
        try {
            Bootstrap.bootstrap();


            ReservationManager rm = ResourceManager.getInstance();
            String resID1 = rm.initiateReservation((short) 1);
            ReservationProposal proposal1 = new ReservationProposal(resID1);
            proposal1.setTemplate("PEX CentOS");
            proposal1.setCPUs(1);
            proposal1.setNumInstances(1);
            Calendar startTime = Calendar.getInstance();
            startTime.add(Calendar.SECOND, 5 * 60);
            proposal1.setStartTime(startTime);
            proposal1.setDuration(15 * 60 * 1000);

            ReservationReply reply = rm.requestReservation(proposal1.getId(), proposal1);

            if (reply.getReply() == ReservationReply.ReservationReplyType.ACCEPT) {
                System.out.println("Reservation reply is " + reply.getReply().toString());
                rm.confirmReservation(proposal1.getId(), proposal1);
            }
//				resM.startVM(resID1, "bloi");

        } catch (PexException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
