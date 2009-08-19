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
package org.unimelb.openpex.rest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.ezmorph.ObjectMorpher;

/**
 *
 * @author brobergj
 */
public class HTTPDateMorpher implements ObjectMorpher {

    private Class dateClass;
    private static final String RFC_1123_DATE_FORMAT =
            "E, dd MMM yyyy HH:mm:ss z";
    private static final String RFC_1123_TIMEZONE = "GMT";
    private DateFormat dateFormat;

    public HTTPDateMorpher(Class dateClass) {
        if (dateClass == null) {
            throw new IllegalArgumentException("dateClass is null");
        }
        if (!Date.class.isAssignableFrom(dateClass)) {
            throw new IllegalArgumentException("dateClass is not an Date class");
        }
        this.dateClass = dateClass;

        this.setDateFormat(new SimpleDateFormat(RFC_1123_DATE_FORMAT));
        this.getDateFormat().setTimeZone(TimeZone.getTimeZone(RFC_1123_TIMEZONE));
    }

    public Object morph(Object value) {
        if (value == null) {
            return dateClass.cast(null);
        }

        System.err.println("Converting String date to Java Date");
        Date date = null;

        try {
            date = getDateFormat().parse((String) value);
        } catch (ParseException ex) {
            Logger.getLogger(JsonHTTPDateValueProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            Logger.getLogger(JsonHTTPDateValueProcessor.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
        return date;
    }

    public Class morphsTo() {
        return dateClass;
    }

    public boolean supports(Class clazz) {
        return String.class.isAssignableFrom(clazz);
    }

    /**
     * @return the dateFormat
     */
    public DateFormat getDateFormat() {
        return dateFormat;
    }

    /**
     * @param dateFormat the dateFormat to set
     */
    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }
}