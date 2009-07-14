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

package org.unimelb.openpex.test;

import org.unimelb.openpex.storage.*;
import org.unimelb.openpex.reservation.ReservationEntity;
import java.util.Calendar;
import java.util.List;
import org.unimelb.openpex.Constants.ReservationStatus;
import org.unimelb.openpex.reservation.InstanceType;
import org.unimelb.openpex.util.RandomGUID;

/**
 *
 * @author srikumar
 */
public class TryQuery{
    
    public static void tryQuery() throws PexStorageFailedException{
        ReservationEntity res = new ReservationEntity((new RandomGUID()).toString());
        res.setType(InstanceType.SMALL);
        res.setTemplate("Provisioning Xen ");
        res.setNumInstancesFixed((short)1);
        res.setNumInstancesOption((short)0);
        res.setStartTime(Calendar.getInstance().getTime());
        res.setEndTime(Calendar.getInstance().getTime());
//        res.setUserid((short)100);
        res.setStatus(ReservationStatus.ACTIVATED);
        PexStorage store = PexStorage.getInstance();
        store.saveReservation(res);
        res.setTemplate("borkee");
        res.setStatus(ReservationStatus.ACCEPTED);
        store.saveReservation(res);
        store.getReservation(res.getRequestId());
        List<ReservationEntity> resList = store.getReservationsbyUserid((short)100);
        System.out.println("Results returned "+resList.size());
        store.deleteReservation(res.getRequestId());
        store.closeEntityManager();
    }

}
