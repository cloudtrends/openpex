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

package org.unimelb.openpex;

public class Constants {

    public static final int SYNCH_INT = 10000;
    public static final String PEX_HIBERNATE_CONFIG = "pex.hibernate.config";
    public static final String PEX_PROPERTIES = "pex.properties";
    public static final String DEFAULT_DB_PATH = "PEX.Persistence";
    public static final String PEX_LOGGER_CONFIG = "log4j.properties";
    /* Copied from the broker*/
    public static final String DB_MODE_AUTO = "AUTO";

    public enum ReservationStatus {

        INITIATED, SUBMITTED, ACCEPTED, COUNTERED, REJECTED, CONFIRMED, ACTIVATED, EXPIRED
    };
}
